package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.time.Duration

@Suppress("EmptyDefaultConstructor")
@InternalReaktiveApi
expect open class ConditionLock() {

    fun lock()

    fun unlock()

    /**
     * Causes the current thread to wait until it is signalled or interrupted,
     * or the specified waiting time elapses.
     *
     * @param timeout the maximum time to wait.
     * @return an estimate of the [timeout] value minus the time spent waiting upon return from this method.
     * A positive value may be used as the argument to a subsequent call to this method to finish waiting out
     * the desired time. A value less than or equal to zero indicates that no time remains.
     */
    fun await(timeout: Duration = Duration.INFINITE): Duration

    /** Wakes up all waiting threads. */
    fun signal()
}
