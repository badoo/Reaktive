package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import kotlinx.coroutines.CoroutineScope

fun <T> singleFromCoroutine(block: suspend CoroutineScope.() -> T): Single<T> =
    single { emitter ->
        launchCoroutine(
            setDisposable = emitter::setDisposable,
            onSuccess = emitter::onSuccess,
            onError = emitter::onError,
            block = block
        )
    }

fun <T> (suspend () -> T).asSingle(): Single<T> = singleFromCoroutine { this@asSingle() }

fun <T> (suspend CoroutineScope.() -> T).asSingle(): Single<T> = singleFromCoroutine(this)
