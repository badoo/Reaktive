package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completable
import kotlinx.coroutines.CoroutineScope

fun completableFromCoroutine(block: suspend CoroutineScope.() -> Unit): Completable =
    completable { emitter ->
        launchCoroutine(
            setDisposable = emitter::setDisposable,
            onSuccess = { emitter.onComplete() },
            onError = emitter::onError,
            block = block
        )
    }

fun (suspend () -> Unit).asCompletable(): Completable = completableFromCoroutine { this@asCompletable() }

fun (suspend CoroutineScope.() -> Unit).asCompletable(): Completable = completableFromCoroutine(this)