package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.disposable.Disposable

fun Completable.onErrorComplete(): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver, CompleteCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.onComplete()
                }
            }
        )
    }
