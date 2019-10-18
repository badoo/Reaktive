package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun <T> Flow<T>.asObservable(): Observable<T> =
    observable { emitter ->
        launchCoroutine(
            setDisposable = emitter::setDisposable,
            onSuccess = { emitter.onComplete() },
            onError = emitter::onError
        ) {
            collect { emitter.onNext(it) }
        }
    }
    