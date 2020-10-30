package com.badoo.reaktive.utils.lock

@Suppress("EmptyDefaultConstructor")
expect class Lock constructor() {

    fun acquire()

    fun release()

    fun destroy()

    /**
     * Returns a [Condition] instance for use with this `Lock` instance.
     *
     * ⚠️ Please note that this method is not available in JavaScript due to its single threaded nature.
     * A runtime exception will be thrown when this method is called in JavaScript.
     */
    fun newCondition(): Condition
}
