package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.completable
import com.badoo.reaktive.disposable.Disposable

fun Maybe<*>.asCompletable(): Completable =
    completable { emitter ->
        subscribe(
            object : MaybeObserver<Any?>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: Any?) {
                    emitter.onComplete()
                }
            }
        )
    }
