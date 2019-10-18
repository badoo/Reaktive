package com.badoo.reaktive.base

/**
 * A common callback for listening for completions with a value
 */
interface SuccessCallback<in T> {

    /**
     * Notifies the host (typically an [Observer]) about completion with a value
     */
    fun onSuccess(value: T)
}
