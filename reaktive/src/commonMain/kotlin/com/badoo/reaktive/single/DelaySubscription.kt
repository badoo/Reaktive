package com.badoo.reaktive.single

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler

/**
 * Delays the actual subscription to the [Single] for the specified time.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#delaySubscription-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun <T> Single<T>.delaySubscription(delayMillis: Long, scheduler: Scheduler): Single<T> {
    require(delayMillis >= 0L) { "delayMillis must not be negative" }

    return single { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)

        executor.submit(delayMillis) {
            emitter.tryCatch {
                subscribe(
                    object : SingleObserver<T>, SingleCallbacks<T> by emitter {
                        override fun onSubscribe(disposable: Disposable) {
                            emitter.setDisposable(disposable)
                        }
                    }
                )
            }
        }
    }
}
