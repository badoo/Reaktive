package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable

fun <T> observableDefer(supplier: () -> Observable<T>): Observable<T> =
    observable { emitter ->
        supplier().subscribe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }
