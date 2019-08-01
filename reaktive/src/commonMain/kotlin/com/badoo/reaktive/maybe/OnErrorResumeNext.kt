package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> Maybe<T>.onErrorResumeNext(nextSupplier: (Throwable) -> Maybe<T>): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, SuccessCallback<T> by observer, CompleteCallback by observer {

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onError(error: Throwable) {
                    observer.tryCatch({ nextSupplier(error) }) {
                        it.subscribeSafe(
                            object : MaybeObserver<T>, MaybeCallbacks<T> by observer {
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

fun <T> Maybe<T>.onErrorResumeNext(next: Maybe<T>): Maybe<T> =
    onErrorResumeNext { next }