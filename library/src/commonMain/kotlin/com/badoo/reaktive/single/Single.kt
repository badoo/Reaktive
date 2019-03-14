package com.badoo.reaktive.single

/**
 * Produces a single value
 */
interface Single<out T> {

    /**
     * Subscribes the specified [SingleObserver] to this [Single]
     *
     * @param observer the [SingleObserver] to be subscribed
     */
    fun subscribe(observer: SingleObserver<T>)
}