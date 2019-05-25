package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.base.subscribeSafe

fun <T> Maybe<Iterable<T>>.flatten(): Observable<T> =
    observable { emitter ->
        subscribeSafe(
            object : MaybeObserver<Iterable<T>>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: Iterable<T>) {
                    value.forEach(emitter::onNext)
                    emitter.onComplete()
                }
            }
        )
    }
