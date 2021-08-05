package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler

/**
 * Delays `onSuccess` and `onComplete` signals from the current [Maybe] for the specified time.
 * The `onError` signal is not delayed by default, which can be enabled by setting the [delayError] flag.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#delay-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun <T> Maybe<T>.delay(delayMillis: Long, scheduler: Scheduler, delayError: Boolean = false): Maybe<T> =
    maybe { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        subscribe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onSuccess(value: T) {
                    executor.submit(delayMillis) {
                        emitter.onSuccess(value)
                    }
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
