package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.base.exceptions.TimeoutException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.scheduler.Scheduler

/**
 * Disposes the current [Maybe] if it does not signal within the [timeoutMillis] timeout,
 * and subscribes to [other] [Maybe] if provided.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#timeout-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-io.reactivex.MaybeSource-).
 */
fun <T> Maybe<T>.timeout(timeoutMillis: Long, scheduler: Scheduler, other: Maybe<T>? = null): Maybe<T> =
    maybe { emitter ->
        val onTimeout: () -> Unit =
            {
                if (other != null) {
                    emitter.setDisposable(null)
                    other.subscribe(
                        object : MaybeObserver<T>, MaybeCallbacks<T> by emitter {
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
            object : CompositeDisposableObserver(), MaybeObserver<T>, MaybeCallbacks<T> by emitter {
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
