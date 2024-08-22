package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.subject.publish.PublishSubject
import com.badoo.reaktive.utils.atomic.AtomicBoolean

/**
 * Returns an [Observable] that automatically resubscribes to this [Observable] if it signals `onError`
 * and the [Observable] returned by the [handler] function emits a value for that specific [Throwable].
 *
 * Please refer to the corresponding RxJava [document](https://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#retryWhen-io.reactivex.functions.Function-).
 */
fun <T> Observable<T>.retryWhen(handler: (Observable<Throwable>) -> Observable<*>): Observable<T> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        val errorSubject = PublishSubject<Throwable>()
        val isError = AtomicBoolean()

        val disposableObserver =
            object : SerialDisposable(), ObservableObserver<T>, ValueCallback<T> by emitter, CompleteCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    replace(disposable)
                }

                override fun onError(error: Throwable) {
                    replace(null)
                    isError.value = true
                    errorSubject.onNext(error)
                }
            }

        disposables += disposableObserver

        handler(errorSubject).subscribe(
            object : ObservableObserver<Any?>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onNext(value: Any?) {
                    if (isError.compareAndSet(true, false)) {
                        subscribe(disposableObserver)
                    }
                }
            }
        )

        subscribe(disposableObserver)
    }
