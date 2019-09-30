package com.badoo.reaktive.observable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.uptimeMillis

private val getTimeMillis = ::uptimeMillis

fun <T> Observable<T>.throttle(windowMillis: Long): Observable<T> = throttle(windowMillis, getTimeMillis)

internal fun <T> Observable<T>.throttle(windowMillis: Long, getTimeMillis: () -> Long): Observable<T> =
    observable { emitter ->
        subscribeSafe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                private val lastTime = AtomicLong(-windowMillis)

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    val time = getTimeMillis()
                    if (time - lastTime.value >= windowMillis) {
                        lastTime.value = time
                        emitter.onNext(value)
                    }
                }
            }
        )
    }