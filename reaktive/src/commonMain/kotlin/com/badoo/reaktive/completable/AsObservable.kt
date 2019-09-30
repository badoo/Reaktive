package com.badoo.reaktive.completable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable

fun <T> Completable.asObservable(): Observable<T> =
    observable { emitter ->
        subscribeSafe(
            object : CompletableObserver, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }