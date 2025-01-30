package com.badoo.reaktive.base

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.utils.handleReaktiveError
import com.badoo.reaktive.utils.throwIfFatal

inline fun <T> ErrorCallback.tryCatch(
    block: () -> T,
    errorTransformer: (Throwable) -> Throwable = { it },
    onSuccess: (T) -> Unit
) {
    try {
        block()
    } catch (e: Throwable) {
        e.throwIfFatal()

        val transformedError =
            try {
                errorTransformer(e)
            } catch (e2: Throwable) {
                e2.throwIfFatal()
                CompositeException(cause1 = e, cause2 = e2)
            }

        handleReaktiveError(transformedError, ::onError)

        return
    }
        .also(onSuccess)
}

inline fun ErrorCallback.tryCatch(
    errorTransformer: (Throwable) -> Throwable = { it },
    block: () -> Unit
) {
    try {
        block()
    } catch (e: Throwable) {
        e.throwIfFatal()

        val transformedError =
            try {
                errorTransformer(e)
            } catch (e2: Throwable) {
                e2.throwIfFatal()
                CompositeException(cause1 = e, cause2 = e2)
            }

        handleReaktiveError(transformedError, ::onError)
    }
}

internal inline fun tryCatchAndHandle(
    errorTransformer: (Throwable) -> Throwable = { it },
    block: () -> Unit
) {
    try {
        block()
    } catch (e: Throwable) {
        e.throwIfFatal()

        val transformedError =
            try {
                errorTransformer(e)
            } catch (e2: Throwable) {
                e2.throwIfFatal()
                CompositeException(cause1 = e, cause2 = e2)
            }

        handleReaktiveError(transformedError)
    }
}
