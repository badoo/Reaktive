package com.badoo.reaktive.single

/**
 * Represents a source that can complete with a value or produce an error.
 * See [SingleCallbacks] for more information.
 */
interface Single<out T> {

    /**
     * Subscribes the specified [SingleObserver] to this [Single]
     *
     * @param observer the [SingleObserver] to be subscribed
     */
    fun subscribe(observer: SingleObserver<T>)
}