package com.badoo.reaktive.utils

internal fun handleSourceError(error: Throwable, onError: ((Throwable) -> Unit)?) {
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
        println("Error delivering uncaught error: e")
    }
}