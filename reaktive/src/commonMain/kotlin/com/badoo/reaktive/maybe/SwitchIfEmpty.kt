package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asMaybe

fun <T> Maybe<T>.switchIfEmpty(other: Maybe<T>): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, SuccessCallback<T> by observer, ErrorCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    other.subscribeSafe(
                        object : MaybeObserver<T>, Observer by this, MaybeCallbacks<T> by observer {
                        }
                    )
                }
            }
        )
    }

fun <T> Maybe<T>.switchIfEmpty(other: Single<T>): Single<T> =
    switchIfEmpty(other.asMaybe())
        .asSingleOrError()