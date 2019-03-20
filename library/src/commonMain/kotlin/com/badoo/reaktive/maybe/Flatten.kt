package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observableByEmitter

fun <T> Maybe<Iterable<T>>.flatten(): Observable<T> =
    observableByEmitter { emitter ->
        subscribeSafe(
            object : MaybeObserver<Iterable<T>> {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: Iterable<T>) {
                    value.forEach(emitter::onNext)
                    emitter.onComplete()
                }

                override fun onComplete() {
                    emitter.onComplete()
                }

                override fun onError(error: Throwable) {
                    emitter.onError(error)
                }
            }
        )
    }
