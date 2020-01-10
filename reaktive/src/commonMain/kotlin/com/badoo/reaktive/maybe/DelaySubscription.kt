package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler

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
