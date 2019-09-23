package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

fun <T> singleFromCoroutine(context: CoroutineContext = Dispatchers.Unconfined, block: suspend () -> T): Single<T> =
    single { emitter ->
        launchCoroutine(
            context = context,
            onSuccess = emitter::onSuccess,
            onError = emitter::onError,
            block = block
        )
            .also(emitter::setDisposable)
    }

fun <T> (suspend () -> T).asSingle(context: CoroutineContext = Dispatchers.Unconfined): Single<T> =
    singleFromCoroutine(context, this)