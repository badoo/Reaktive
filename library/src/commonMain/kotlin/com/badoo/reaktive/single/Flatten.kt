package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observableByEmitter

fun <T> Single<Iterable<T>>.flatten(): Observable<T> =
    observableByEmitter { emitter ->
        subscribeSafe(
            object : SingleObserver<Iterable<T>> {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: Iterable<T>) {
                    value.forEach(emitter::onNext)
                    emitter.onComplete()
                }

                override fun onError(error: Throwable) {
                    emitter.onError(error)
                }
            }
        )
    }