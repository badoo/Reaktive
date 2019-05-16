package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.update

fun <T> Observable<T>.skip(count: Long): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by observer {
                private var remaining = AtomicReference(count)
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onNext(value: T) {
                    if (remaining.value != 0L) {
                        remaining.update { it - 1 }
                    } else {
                        observer.onNext(value)
                    }
                }
            }
        )
    }