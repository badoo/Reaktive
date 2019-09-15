package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext

fun <T> Flow<T>.asObservable(context: CoroutineContext = Dispatchers.Unconfined): Observable<T> =
    observable { emitter ->
        launchCoroutine(
            context = context,
            onSuccess = { emitter.onComplete() },
            onError = emitter::onError
        ) {
            collect { emitter.onNext(it) }
        }
            .also(emitter::setDisposable)
    }