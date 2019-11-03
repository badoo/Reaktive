package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.clock.Clock
import com.badoo.reaktive.utils.clock.DefaultClock

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
