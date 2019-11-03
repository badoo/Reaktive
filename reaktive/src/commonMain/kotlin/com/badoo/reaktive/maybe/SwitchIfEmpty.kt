package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asMaybe

fun <T> Maybe<T>.switchIfEmpty(other: Maybe<T>): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, SuccessCallback<T> by emitter, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    other.subscribeSafe(
                        object : MaybeObserver<T>, Observer by this, MaybeCallbacks<T> by emitter {
                        }
                    )
                }
            }
        )
    }

fun <T> Maybe<T>.switchIfEmpty(other: Single<T>): Single<T> =
    switchIfEmpty(other.asMaybe())
        .asSingleOrError()
