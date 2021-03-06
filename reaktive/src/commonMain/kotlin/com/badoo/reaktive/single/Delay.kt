package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler

/**
 * Delays `onSuccess` or `onError` signals from the current [Single] for the specified time.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#delay-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-boolean-).
 */
fun <T> Single<T>.delay(delayMillis: Long, scheduler: Scheduler, delayError: Boolean = false): Single<T> =
    single { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        subscribe(
            object : SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onSuccess(value: T) {
                    executor.submit(delayMillis) {
                        emitter.onSuccess(value)
                    }
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
