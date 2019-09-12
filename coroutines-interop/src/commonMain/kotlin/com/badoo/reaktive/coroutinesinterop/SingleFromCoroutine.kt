package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun <T> singleFromCoroutine(context: CoroutineContext = Dispatchers.Unconfined, block: suspend () -> T): Single<T> =
    single { emitter ->
        GlobalScope
            .launch {
                emitter.onSuccess(
                    try {
                        block()
                    } catch (e: Throwable) {
                        emitter.onError(e)
                        return@launch
                    }
                )
            }
            .asDisposable()
            .also(emitter::setDisposable)
    }

fun <T> (suspend () -> T).asSingle(context: CoroutineContext = Dispatchers.Unconfined): Single<T> =
    singleFromCoroutine(context, this)