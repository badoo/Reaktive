package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T, R> Maybe<T>.flatMap(mapper: (T) -> Maybe<R>): Maybe<R> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, CompletableCallbacks by observer {
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
                            object : MaybeObserver<R> by observer {
                                override fun onSubscribe(disposable: Disposable) {
                                    disposableWrapper.set(disposable)
                                }
                            }
                        )
                }
            }
        )
    }