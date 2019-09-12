package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completable
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

fun completableFromCoroutine(context: CoroutineContext = Dispatchers.Unconfined, block: suspend () -> Unit): Completable =
    completable { emitter ->
        launchCoroutine(
            context = context,
            onSuccess = { emitter.onComplete() },
            onError = emitter::onError,
            block = block
        )
            .also(emitter::setDisposable)
    }

fun (suspend () -> Unit).asCompletable(context: CoroutineContext = Dispatchers.Unconfined): Completable =
    completableFromCoroutine(context, this)