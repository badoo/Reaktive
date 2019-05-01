package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable

fun <T> Maybe<Iterable<T>>.flatten(): Observable<T> =
    observable { emitter ->
        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<Iterable<T>>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: Iterable<T>) {
                    value.forEach(emitter::onNext)
                    emitter.onComplete()
                }
            }
        )
    }
