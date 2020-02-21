package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe
import kotlinx.coroutines.CoroutineScope

/**
 * Launches coroutine and calls the provided block for every subscription.
 * Please note that it uses `runBlocking` in Kotlin/Native.
 * Please read the [README](https://github.com/badoo/Reaktive#coroutines-interop) for more information.
 */
fun <T> maybeFromCoroutine(block: suspend CoroutineScope.() -> T): Maybe<T> =
    maybe { emitter ->
        launchCoroutine(
            setDisposable = emitter::setDisposable,
            onSuccess = emitter::onSuccess,
            onError = emitter::onError,
            block = block
        )
    }

fun <T> (suspend () -> T).asMaybe(): Maybe<T> = maybeFromCoroutine { this@asMaybe() }

fun <T> (suspend CoroutineScope.() -> T).asMaybe(): Maybe<T> = maybeFromCoroutine(this)
