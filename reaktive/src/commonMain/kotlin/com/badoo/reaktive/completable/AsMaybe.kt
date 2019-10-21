package com.badoo.reaktive.completable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe

fun <T> Completable.asMaybe(): Maybe<T> =
    maybe { emitter ->
        subscribeSafe(
            object : CompletableObserver, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }
