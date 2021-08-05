package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler

/**
 * Delays the actual subscription to the [Maybe] for the specified time.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#delaySubscription-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun <T> Maybe<T>.delaySubscription(delayMillis: Long, scheduler: Scheduler): Maybe<T> {
    require(delayMillis >= 0L) { "delayMillis must not be negative" }

    return maybe { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)

        executor.submit(delayMillis) {
            emitter.tryCatch {
                subscribe(
                    object : MaybeObserver<T>, MaybeCallbacks<T> by emitter {
                        override fun onSubscribe(disposable: Disposable) {
                            emitter.setDisposable(disposable)
                        }
                    }
                )
            }
        }
    }
}
