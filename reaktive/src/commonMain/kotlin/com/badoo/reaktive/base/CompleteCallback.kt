package com.badoo.reaktive.base

/**
 * A common callback for listening for completions
 */
interface CompleteCallback {

    /**
     * Notifies the host (typically an [Observer]) about completion
     */
    fun onComplete()
}