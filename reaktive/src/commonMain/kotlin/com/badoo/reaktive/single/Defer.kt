package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable

fun <T> singleDefer(supplier: () -> Single<T>): Single<T> =
    single { emitter ->
        supplier().subscribe(
            object : SingleObserver<T>, SingleCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }
