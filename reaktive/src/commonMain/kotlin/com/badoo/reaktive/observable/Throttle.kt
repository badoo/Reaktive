package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.clock.Clock
import com.badoo.reaktive.utils.clock.DefaultClock

/**
 * Returns an [Observable] that emits only the first element emitted by the source [Observable] during a time window
 * defined by [windowMillis], which begins with the emitted element.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#throttleFirst-long-java.util.concurrent.TimeUnit-).
 */
fun <T> Observable<T>.throttle(windowMillis: Long): Observable<T> = throttle(windowMillis, DefaultClock)

internal fun <T> Observable<T>.throttle(windowMillis: Long, clock: Clock): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                private val lastTime = AtomicLong(-windowMillis)

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    val time = clock.uptimeMillis
                    if (time - lastTime.value >= windowMillis) {
                        lastTime.value = time
                        emitter.onNext(value)
                    }
                }
            }
        )
    }
