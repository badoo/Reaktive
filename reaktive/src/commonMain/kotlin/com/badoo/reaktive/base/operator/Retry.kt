package com.badoo.reaktive.base.operator

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.utils.atomic.AtomicInt

internal class Retry(
    private val emitter: ErrorCallback,
    private val predicate: (attempt: Int, Throwable) -> Boolean
) {
    private val attempt = AtomicInt(0)

    fun onError(error: Throwable, resubscribe: () -> Unit) {
        emitter.tryCatch(
            { predicate(attempt.addAndGet(1), error) },
            { CompositeException(error, it) }
        ) { shouldRetry ->
            if (shouldRetry) {
                resubscribe()
            } else {
                emitter.onError(error)
            }
        }
    }
}
