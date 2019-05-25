package com.badoo.reaktive.base

/**
 * Represents a generic source that can be subscribed with an [Observer]
 */
interface Source<in T: Observer> {

    /**
     * Subscribes the specified [Observer] to this [Source]
     *
     * @param observer the [Observer] to be subscribed
     */
    fun subscribe(observer: T)
}