package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> Flow<T>.asObservable(): Observable<T> =
    observable { emitter ->
        val scope = CoroutineScope(Dispatchers.Unconfined)
        emitter.setDisposable(scope.asDisposable())

        scope.launch {
            try {
                collect { emitter.onNext(it) }
            } catch (e: Throwable) {
                emitter.onError(e)
            } finally {
                emitter.onComplete()
            }
        }
    }