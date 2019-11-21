package com.badoo.reaktive.utils

import com.badoo.reaktive.base.exceptions.CompositeException

fun handleReaktiveError(error: Throwable, onError: ((Throwable) -> Unit)? = null) {
    if (onError == null) {
        handleError(error)
    } else {
        handleError(error, onError)
    }
}

private fun handleError(error: Throwable) {
    try {
        reaktiveUncaughtErrorHandler(error)
    } catch (errorDeliveryException: Throwable) {
        printErrors("Error delivering uncaught error", error, errorDeliveryException)
    }
}

private fun handleError(error: Throwable, onError: (Throwable) -> Unit) {
    try {
        onError(error)
    } catch (errorHandlerException: Throwable) {
        printErrors("onError callback failed", error, errorHandlerException)

        try {
            reaktiveUncaughtErrorHandler(CompositeException(error, errorHandlerException))
        } catch (errorDeliveryException: Throwable) {
            printErrors("Error delivering uncaught error", error, errorDeliveryException)
        }
    }
}

private fun printErrors(message: String, outerError: Throwable, innerError: Throwable) {
    printError("$message ($outerError): $innerError")
    outerError.printStack()
    innerError.printStack()
}
