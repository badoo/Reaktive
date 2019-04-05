package com.badoo.reaktive.base

/**
 * A common callback for listening for exceptions
 */
interface ErrorCallback {

    /**
     * Called when there is an exception occurred
     *
     * @param error the exception
     */
    fun onError(error: Throwable)
}