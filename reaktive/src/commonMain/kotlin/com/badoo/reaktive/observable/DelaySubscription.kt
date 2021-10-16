package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler

/**
 * Delays the actual subscription to the [Observable] for the specified time.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#delaySubscription-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun <T> Observable<T>.delaySubscription(delayMillis: Long, scheduler: Scheduler): Observable<T> {
    require(delayMillis >= 0L) { "delayMillis must not be negative" }

    return observable { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)

        executor.submit(delayMillis) {
            emitter.tryCatch {
                subscribe(
                    object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                        override fun onSubscribe(disposable: Disposable) {
                            emitter.setDisposable(disposable)
                        }
                    }
                )
            }
        }
    }
}
