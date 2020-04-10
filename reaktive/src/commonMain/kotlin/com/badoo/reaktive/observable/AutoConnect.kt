package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T> ConnectableObservable<T>.autoConnect(subscriberCount: Int = 1): Observable<T> {
    if (subscriberCount <= 0) {
        connect()
        return this
    }

    val subscribeCount = AtomicInt()

    return observable { emitter ->
        this@autoConnect.subscribe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )

        if (subscribeCount.addAndGet(1) == subscriberCount) {
            this@autoConnect.connect()
        }
    }
}
