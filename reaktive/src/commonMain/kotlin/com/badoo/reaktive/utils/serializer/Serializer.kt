package com.badoo.reaktive.utils.serializer

/**
 * Serializes all calls to [Serializer.accept] method.
 * Each implementation should normally have a callback which should be synchronously called with corresponding values.
 */
internal interface Serializer<in T> {

    /**
     * Either calls "onValue" with the specified value or queues the value.
     * This method is supposed to be called from multiple threads.
     * If there are no threads currently processing any value then this thread will process the specified value.
     * Otherwise value will be queued and processed later by existing thread.
     *
     * @param value the value
     */
    fun accept(value: T)

    /**
     * Clears the queue
     */
    fun clear()
}
