package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

fun <T> Maybe<T>.filter(predicate: (T) -> Boolean): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.tryCatch(block = { predicate(value) }) {
                        if (it) {
                            emitter.onSuccess(value)
                        } else {
                            emitter.onComplete()
                        }
                    }
                }
            }
        )
    }
