package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/**
 * Launches coroutine and collects the [Flow] for every subscription.
 * Please note that it uses `runBlocking` in Kotlin/Native.
 * Please read the [README](https://github.com/badoo/Reaktive#coroutines-interop) for more information.
 */
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
