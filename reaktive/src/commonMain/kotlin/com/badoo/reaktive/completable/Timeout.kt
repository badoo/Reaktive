package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.base.exceptions.TimeoutException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.scheduler.Scheduler

fun Completable.timeout(timeoutMillis: Long, scheduler: Scheduler, other: Completable? = null): Completable =
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
            .submit(timeoutMillis, onTimeout)

        subscribe(upstreamObserver)
    }
