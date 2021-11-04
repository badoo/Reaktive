package com.badoo.reaktive.base.operator

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.utils.atomic.AtomicLong

internal class Retry(
    private val emitter: ErrorCallback,
    private val predicate: (attempt: Long, Throwable) -> Boolean
) {
    private val attempt = AtomicLong(0)

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
