package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompositeDisposableObserver
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.time.Duration

/**
 * Returns an [Observable] that emits only the first element emitted by the source [Observable] during a time window
 * defined by [window], which begins with the emitted element.
 *
 * Values are emitted on the upstream thread, the [scheduler] is used only for timings.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#throttleFirst-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun <T> Observable<T>.throttle(window: Duration, scheduler: Scheduler = computationScheduler): Observable<T> {
    if (!window.isPositive()) {
        return this
    }

    return observable { emitter ->
        subscribe(
            object : CompositeDisposableObserver(), ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                private val executor = scheduler.newExecutor().addTo(this)
                private val gate = AtomicBoolean()

                init {
                    emitter.setDisposable(this)
                }

                override fun onNext(value: T) {
                    if (gate.compareAndSet(false, true)) {
                        emitter.onNext(value)
                        executor.submit(delay = window) { gate.value = false }
                    }
                }
            }
        )
    }
}
