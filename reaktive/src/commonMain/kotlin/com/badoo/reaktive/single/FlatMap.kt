package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T, R> Single<T>.flatMap(mapper: (T) -> Single<R>): Single<R> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T>, Observer by observer, ErrorCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: T) {
                    try {
                        mapper(value)
                    } catch (e: Throwable) {
                        onError(e)
                        return
                    }
                        .subscribeSafe(
                            object : SingleObserver<R> by observer {
                                override fun onSubscribe(disposable: Disposable) {
                                    disposableWrapper.set(disposable)
                                }
                            }
                        )
                }
            }
        )
    }