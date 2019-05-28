package com.badoo.reaktive.base

internal inline fun <T> ErrorCallback.tryCatch(block: () -> T, onSuccess: (T) -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        onError(e)
        return
    }
        .also(onSuccess)
}