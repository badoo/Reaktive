package com.badoo.reaktive.utils

internal fun handleSourceError(error: Throwable, onError: ((Throwable) -> Unit)? = null) {
    try {
        if (onError == null) {
            reaktiveUncaughtErrorHandler(error)
        } else {
            try {
                onError(error)
            } catch (e: Throwable) {
                println("onError callback failed: ($error): $e")
                error.printStack()
                reaktiveUncaughtErrorHandler(e)
            }
        }
    } catch (e: Throwable) {
        println("Error delivering uncaught error ($error): $e")
        error.printStack()
        e.printStack()
    }
}

internal fun <T> List<T>.replace(index: Int, element: T): List<T> =
    mapIndexed { i, item -> if (i == index) element else item }

internal object Uninitialized