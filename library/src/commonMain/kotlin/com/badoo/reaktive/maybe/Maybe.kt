package com.badoo.reaktive.maybe

/**
 * Produces either a single value or just completes
 */
interface Maybe<out T> {

    /**
     * Subscribes the specified [MaybeObserver] to this [Maybe]
     *
     * @param observer the [MaybeObserver] to be subscribed
     */
    fun subscribe(observer: MaybeObserver<T>)
}