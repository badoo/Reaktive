package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable

fun <T> Maybe<T>.asObservable(): Observable<T> =
    observable { emitter ->
        subscribe(
            object : MaybeObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.onNext(value)
                    emitter.onComplete()
                }
            }
        )
    }
