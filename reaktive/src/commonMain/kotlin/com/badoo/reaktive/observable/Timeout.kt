package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.base.exceptions.TimeoutException
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.scheduler.Scheduler

/**
 * Returns an [Observable] that emits elements from the source [Observable] and counts a timeout specified by
 * [timeoutMillis]. If the timeout ever hits, disposes the source [Observable] and subscribes to the
 * [other][other] [Observable], if any.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#timeout-io.reactivex.functions.Function-io.reactivex.ObservableSource-).
 */
fun <T> Observable<T>.timeout(timeoutMillis: Long, scheduler: Scheduler, other: Observable<T>? = null): Observable<T> =
    observable { emitter ->
        val onTimeout: () -> Unit =
            {
                if (other != null) {
                    emitter.setDisposable(null)
                    other.subscribe(
                        object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                            override fun onSubscribe(disposable: Disposable) {
                                emitter.setDisposable(disposable)
                            }
                        }
                    )
                } else {
                    emitter.onError(TimeoutException())
                }
            }

        val upstreamObserver =
            object : CompositeDisposableObserver(), ObservableObserver<T>, CompletableCallbacks by emitter {
                private val executor = scheduler.newExecutor().addTo(this)

                override fun onNext(value: T) {
                    executor.cancel()
                    emitter.onNext(value)
                    startTimeout()
                }

                fun startTimeout() {
                    executor.submit(timeoutMillis, onTimeout)
                }
            }

        emitter.setDisposable(upstreamObserver)
        upstreamObserver.startTimeout()

        subscribe(upstreamObserver)
    }
