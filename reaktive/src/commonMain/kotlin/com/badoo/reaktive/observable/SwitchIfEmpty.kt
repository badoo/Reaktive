package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomicreference.AtomicReference

fun <T> Observable<T>.switchIfEmpty(otherObservable: Observable<T>): Observable<T> =
    switchIfEmpty { otherObservable }

fun <T> Observable<T>.switchIfEmpty(otherObservable: () -> Observable<T>): Observable<T> =
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
                        otherObservable().subscribeSafe(
                            object : ObservableObserver<T>, ErrorCallback by this {
                                override fun onSubscribe(disposable: Disposable) {
                                    disposableWrapper.set(disposable)
                                }

                                override fun onNext(value: T) {
                                    observer.onNext(value)
                                }

                                override fun onComplete() {
                                    observer.onComplete()
                                }
                            }
                        )
                    } else {
                        observer.onComplete()
                    }
                }
            }
        )
    }