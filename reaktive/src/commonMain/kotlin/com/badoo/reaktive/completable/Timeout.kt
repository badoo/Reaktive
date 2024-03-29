package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.base.exceptions.TimeoutException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.scheduler.Scheduler
import kotlin.time.Duration

/**
 * Disposes the current [Completable] if it does not signal within the [timeout], and subscribes to [other] [Completable] if provided.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#timeout-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-io.reactivex.CompletableSource-).
 */
fun Completable.timeout(timeout: Duration, scheduler: Scheduler, other: Completable? = null): Completable =
    completable { emitter ->
        val onTimeout: () -> Unit =
            {
                if (other != null) {
                    emitter.setDisposable(null)
                    other.subscribe(
                        object : CompletableObserver, CompletableCallbacks by emitter {
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
            object : CompositeDisposableObserver(), CompletableObserver, CompletableCallbacks by emitter {
            }

        emitter.setDisposable(upstreamObserver)

        scheduler
            .newExecutor()
            .addTo(upstreamObserver)
            .submit(delay = timeout, task = onTimeout)

        subscribe(upstreamObserver)
    }
