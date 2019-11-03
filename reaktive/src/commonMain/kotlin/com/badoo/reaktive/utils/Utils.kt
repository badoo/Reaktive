@file:Suppress("MatchingDeclarationName")

package com.badoo.reaktive.utils

import com.badoo.reaktive.base.exceptions.CompositeException

@PublishedApi
internal fun handleSourceError(error: Throwable, onError: ((Throwable) -> Unit)? = null) {
    try {
        if (onError == null) {
            reaktiveUncaughtErrorHandler(error)
        } else {
            try {
                onError(error)
            } catch (errorHandlerException: Throwable) {
                printError("onError callback failed ($error): $errorHandlerException")
                error.printStack()
                errorHandlerException.printStack()
                reaktiveUncaughtErrorHandler(CompositeException(error, errorHandlerException))
            }
        }
    } catch (errorDeliveryException: Throwable) {
        printError("Error delivering uncaught error ($error): $errorDeliveryException")
        error.printStack()
        errorDeliveryException.printStack()
    }
}

internal object Uninitialized
