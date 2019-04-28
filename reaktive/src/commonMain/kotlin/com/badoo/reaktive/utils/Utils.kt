package com.badoo.reaktive.utils

internal fun handleSourceError(error: Throwable, onError: ((Throwable) -> Unit)? = null) {
    try {
        if (onError == null) {
            reaktiveUncaughtErrorHandler(error)
        } else {
            try {
                onError(error)
            } catch (ignored: Throwable) {
                reaktiveUncaughtErrorHandler(error)
            }
        }
    } catch (e: Throwable) {
        println("Error delivering uncaught error: $e")
    }
}

internal fun <T> List<T>.replace(index: Int, element: T): List<T> =
    mapIndexed { i, item -> if (i == index) element else item }
