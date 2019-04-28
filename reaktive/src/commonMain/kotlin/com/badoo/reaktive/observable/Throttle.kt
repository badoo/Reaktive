package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.uptimeMillis

fun <T> Observable<T>.throttle(windowMillis: Long): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by observer {
                private val lastTime = AtomicReference(0L)

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onNext(value: T) {
                    val time = uptimeMillis
                    if (time - lastTime.value >= windowMillis) {
                        lastTime.value = time
                        observer.onNext(value)
                    }
                }
            }
        )
    }