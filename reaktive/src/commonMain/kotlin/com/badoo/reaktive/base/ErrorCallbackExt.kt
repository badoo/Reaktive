package com.badoo.reaktive.base

@PublishedApi
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

@PublishedApi
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
