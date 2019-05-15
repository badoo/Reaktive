package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomicreference.AtomicReference

fun <T> Observable<T>.defaultIfEmpty(defaultValue: T): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by observer {
                private val isEmpty = AtomicReference(true)

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onNext(value: T) {
                    isEmpty.value = false
                    observer.onNext(value)
                }

                override fun onComplete() {
                    if (isEmpty.value) {
                        observer.onNext(defaultValue)
                    }

                    observer.onComplete()
                }
            }
        )
    }