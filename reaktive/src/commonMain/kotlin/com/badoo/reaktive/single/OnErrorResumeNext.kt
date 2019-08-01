package com.badoo.reaktive.single

import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> Single<T>.onErrorResumeNext(nextSupplier: (Throwable) -> Single<T>): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T>, SuccessCallback<T> by observer {

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onError(error: Throwable) {
                    observer.tryCatch({ nextSupplier(error) }) {
                        it.subscribeSafe(
                            object : SingleObserver<T>, SingleCallbacks<T> by observer {
                                override fun onSubscribe(disposable: Disposable) {
                                    disposableWrapper.set(disposable)
                                }
                            }
                        )
                    }
                }
            }
        )
    }

fun <T> Single<T>.onErrorResumeNext(next: Single<T>): Single<T> =
    onErrorResumeNext { next }