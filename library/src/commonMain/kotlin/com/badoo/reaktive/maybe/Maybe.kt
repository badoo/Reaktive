package com.badoo.reaktive.maybe

/**
 * Represents a source that can complete with or without a value or produce an error.
 * See [MaybeCallbacks] for more information.
 */
interface Maybe<out T> {

    /**
     * Subscribes the specified [MaybeObserver] to this [Maybe]
     *
     * @param observer the [MaybeObserver] to be subscribed
     */
    fun subscribe(observer: MaybeObserver<T>)
}