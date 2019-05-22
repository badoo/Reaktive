package com.badoo.reaktive.utils

internal fun handleSourceError(error: Throwable, onError: ((Throwable) -> Unit)? = null) {
    try {
        if (onError == null) {
            reaktiveUncaughtErrorHandler(error)
        } else {
            try {
                onError(error)
            } catch (e: Throwable) {
                reaktiveUncaughtErrorHandler(e)
            }
        }
    } catch (e: Throwable) {
        println("Error delivering uncaught error ($error): $e")
    }
}

internal fun <T> List<T>.replace(index: Int, element: T): List<T> =
    mapIndexed { i, item -> if (i == index) element else item }

internal object Uninitialized