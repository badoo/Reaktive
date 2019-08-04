package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T, R> Observable<T>.switchMap(mapper: (T) -> Observable<R>): Observable<R> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val serializedEmitter = emitter.serialize()

        subscribeSafe(object : ObservableObserver<T>, ErrorCallback by serializedEmitter {
            private val activeSourceCount = AtomicInt(initialValue = 1)
            private val activeObserver = DisposableWrapper()

            override fun onSubscribe(disposable: Disposable) {
                disposables += disposable
                disposables += activeObserver
            }

            override fun onNext(value: T) {
                activeSourceCount.addAndGet(delta = 1)

                serializedEmitter.tryCatch({ mapper(value) }) { innerObservable ->
                    val innerObserver = object : ObservableObserver<R>, Disposable,
                        ValueCallback<R> by serializedEmitter,
                        ErrorCallback by this {

                        private val disposableWrapper = DisposableWrapper()
                        private val isCompleted = AtomicBoolean(initialValue = false)

                        override fun onSubscribe(disposable: Disposable) {
                            disposableWrapper.set(disposable)
                        }

                        override fun onComplete() {
                            if (isCompleted.compareAndSet(expectedValue = false, newValue = true)) {
                                if (activeSourceCount.addAndGet(delta = -1) <= 0) {
                                    serializedEmitter.onComplete()
                                }
                            }
                        }

                        override val isDisposed: Boolean get() = disposableWrapper.isDisposed

                        override fun dispose() {
                            disposableWrapper.dispose()
                            if (!isCompleted.value) {
                                activeSourceCount.addAndGet(delta = -1)
                            }
                        }

                    }
                    activeObserver.set(innerObserver)
                    innerObservable.subscribeSafe(innerObserver)
                }
            }

            override fun onComplete() {
                if (activeSourceCount.addAndGet(delta = -1) <= 0) {
                    serializedEmitter.onComplete()
                }
            }

        })
    }

fun <T, U, R> Observable<T>.switchMap(
    mapper: (T) -> Observable<U>,
    resultSelector: (T, U) -> R
): Observable<R> = switchMap { t ->
    mapper(t).map { u -> resultSelector(t, u) }
}