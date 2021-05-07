package com.badoo.reaktive.utils.isolate

internal expect class IsolatedReference<out T : Any>(value: T) : SharedReference<T> {

    /**
     * Throws IncorrectDereferenceException on Native if not disposed
     * and the underlying value was not originally frozen and is accessed on another thread.
     */
    override fun getOrThrow(): T?
}
