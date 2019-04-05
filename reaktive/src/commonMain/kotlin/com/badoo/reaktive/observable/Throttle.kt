package com.badoo.reaktive.observable

import com.badoo.reaktive.utils.uptimeMillis

fun <T> Observable<T>.throttle(windowMillis: Long): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : ObservableObserver<T> by observer {
                private var lastTime = 0L

                override fun onNext(value: T) {
                    val time = uptimeMillis
                    if (time - lastTime >= windowMillis) {
                        lastTime = time
                        observer.onNext(value)
                    }
                }
            }
        )
    }