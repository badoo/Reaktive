package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler

/**
 * Delays `onComplete` signal from the current [Completable] for the specified time.
 * The `onError` signal is not delayed by default, which can be enabled by setting the [delayError] flag.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#delay-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-boolean-).
 */
fun Completable.delay(delayMillis: Long, scheduler: Scheduler, delayError: Boolean = false): Completable =
    completable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        subscribe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onComplete() {
                    executor.submit(delayMillis, emitter::onComplete)
                }

                override fun onError(error: Throwable) {
                    if (delayError) {
                        executor.submit(delayMillis) {
                            emitter.onError(error)
                        }
                    } else {
                        executor.cancel()
                        executor.submit {
                            emitter.onError(error)
                        }
                    }
                }
            }
        )
    }
