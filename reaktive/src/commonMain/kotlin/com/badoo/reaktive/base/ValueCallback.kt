package com.badoo.reaktive.base

/**
 * A common callback for listening for values
 */
fun interface ValueCallback<in T> {

    /**
     * Delivers values to the host (typically an [Observer])
     */
    fun onNext(value: T)
}
