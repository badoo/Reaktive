package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun <T> Flow<T>.asObservable(context: CoroutineContext = Dispatchers.Unconfined): Observable<T> =
    observable { emitter ->
        GlobalScope
            .launch(context) {
                try {
                    collect { emitter.onNext(it) }
                } catch (e: Throwable) {
                    emitter.onError(e)
                } finally {
                    emitter.onComplete()
                }
            }
            .asDisposable()
            .also(emitter::setDisposable)
    }