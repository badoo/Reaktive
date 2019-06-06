package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> Maybe<T>.filter(predicate: (T) -> Boolean): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, Observer by observer, CompletableCallbacks by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: T) {
                    observer.tryCatch({ predicate(value) }) {
                        if (it) {
                            observer.onSuccess(value)
                        } else {
                            observer.onComplete()
                        }
                    }
                }
            }
        )
    }