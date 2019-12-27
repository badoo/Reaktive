package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.base.exceptions.TimeoutException
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.scheduler.Scheduler

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
