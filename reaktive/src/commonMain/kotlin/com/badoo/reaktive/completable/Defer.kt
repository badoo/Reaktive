package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable

fun completableDefer(supplier: () -> Completable): Completable =
    completable { emitter ->
        supplier().subscribe(
            object : CompletableObserver, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }
