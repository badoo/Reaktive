package com.badoo.reaktive.utils.serializer

/**
 * Serializes all calls to "accept" method and synchronously calls "onValue" method with corresponding values
 */
internal expect abstract class Serializer<in T>(
    comparator: Comparator<in T>? = null
) {

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


    /**
     * Called synchronously for every value
     *
     * @param value a value
     * @return true if processing should continue, false otherwise
     */
    protected abstract fun onValue(value: T): Boolean
}