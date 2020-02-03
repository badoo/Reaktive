package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable

fun <T> maybeDefer(supplier: () -> Maybe<T>): Maybe<T> =
    maybe { emitter ->
        supplier().subscribe(
            object : MaybeObserver<T>, MaybeCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }
