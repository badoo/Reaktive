package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun <T> singleFromCoroutine(block: suspend () -> T): Single<T> =
    single { emitter ->
        val scope = CoroutineScope(Dispatchers.Unconfined)
        emitter.setDisposable(scope.asDisposable())

        scope.launch {
            emitter.onSuccess(
                try {
                    block()
                } catch (e: Throwable) {
                    emitter.onError(e)
                    return@launch
                }
            )
        }
    }

fun <T> (suspend () -> T).asSingle(): Single<T> = singleFromCoroutine(this)