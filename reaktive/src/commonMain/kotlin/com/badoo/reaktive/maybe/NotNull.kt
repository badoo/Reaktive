package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

fun <T : Any> Maybe<T?>.notNull(): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T?>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T?) {
                    if (value != null) {
                        emitter.onSuccess(value)
                    } else {
                        emitter.onComplete()
                    }
                }
            }
        )
    }
