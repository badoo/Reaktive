package com.badoo.reaktive.single

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.base.exceptions.TimeoutException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.scheduler.Scheduler

fun <T> Single<T>.timeout(timeoutMillis: Long, scheduler: Scheduler, other: Single<T>? = null): Single<T> =
    single { emitter ->
        val onTimeout: () -> Unit =
            {
                if (other != null) {
                    emitter.setDisposable(null)
                    other.subscribe(
                        object : SingleObserver<T>, SingleCallbacks<T> by emitter {
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
            object : CompositeDisposableObserver(), SingleObserver<T>, SingleCallbacks<T> by emitter {
                private val executor = scheduler.newExecutor().addTo(this)

                override fun onSuccess(value: T) {
                    executor.cancel()
                    emitter.onSuccess(value)
                }

                fun startTimeout() {
                    executor.submit(timeoutMillis, onTimeout)
                }
            }

        emitter.setDisposable(upstreamObserver)
        upstreamObserver.startTimeout()

        subscribe(upstreamObserver)
    }
