package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate

fun <T> Observable<T>.debounce(debounceSelector: (T) -> Completable): Observable<T> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        val innerDisposableWrapper = DisposableWrapper()
        disposables += innerDisposableWrapper

        val serializedEmitter = emitter.serialize()

        subscribe(
            object : ObservableObserver<T>, ErrorCallback by serializedEmitter {
                private val pendingValue = AtomicReference<DebouncePendingValue<T>?>(null)

                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onNext(value: T) {
                    serializedEmitter.tryCatch(
                        block = { debounceSelector(value) },
                        onSuccess = { completable -> onInnerDebouncer(value, completable) }
                    )
                }

                private fun onInnerDebouncer(value: T, completable: Completable) {
                    val newPendingValue = DebouncePendingValue(value)
                    pendingValue.value = newPendingValue

                    val localDisposableWrapper = DisposableWrapper()

                    /*
                     * Dispose any existing inner Completable.
                     * If a previous Completable did not provide its disposable yet
                     * it will be disposed automatically later since
                     * its localDisposableWrapper is disposed.
                     */
                    innerDisposableWrapper.set(localDisposableWrapper)

                    val innerObserver =
                        object : CompletableObserver, ErrorCallback by serializedEmitter {
                            override fun onSubscribe(disposable: Disposable) {
                                localDisposableWrapper.set(disposable)
                            }

                            override fun onComplete() {
                                pendingValue.getAndUpdate { if (it === newPendingValue) null else it }
                                    ?.takeIf { it === newPendingValue }
                                    ?.let { serializedEmitter.onNext(it.value) }
                            }
                        }

                    completable.subscribeSafe(innerObserver)
                }

                override fun onComplete() {
                    pendingValue.getAndUpdate { null }
                        ?.let { serializedEmitter.onNext(it.value) }
                    serializedEmitter.onComplete()
                }
            }
        )
    }
