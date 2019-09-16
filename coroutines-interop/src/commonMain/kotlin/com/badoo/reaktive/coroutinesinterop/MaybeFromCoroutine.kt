package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

fun <T> maybeFromCoroutine(context: CoroutineContext = Dispatchers.Unconfined, block: suspend () -> T): Maybe<T> =
    maybe { emitter ->
        launchCoroutine(
            context = context,
            onSuccess = emitter::onSuccess,
            onError = emitter::onError,
            block = block
        )
            .also(emitter::setDisposable)
    }

fun <T> (suspend () -> T).asMaybe(context: CoroutineContext = Dispatchers.Unconfined): Maybe<T> =
    maybeFromCoroutine(context, this)