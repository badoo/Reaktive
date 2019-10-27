package com.badoo.reaktive.base

internal inline fun <T> ErrorCallback.tryCatch(
    block: () -> T,
    errorTransformer: (Throwable) -> Throwable = { it },
    onSuccess: (T) -> Unit
) {
    try {
        block()
    } catch (e: Throwable) {
        onError(errorTransformer(e))
        return
    }
        .also(onSuccess)
}

internal inline fun ErrorCallback.tryCatch(
    errorTransformer: (Throwable) -> Throwable = { it },
    block: () -> Unit
) {
    try {
        block()
    } catch (e: Throwable) {
        onError(errorTransformer(e))
    }
}
