package com.badoo.reaktive.base

/**
 * Base interface for reactive sources consumable by an [Observer]
 */
interface Source<in T : Observer> {

    /**
     * Subscribes the specified [Observer] to this [Source]
     *
     * @param observer the [Observer]
     */
    fun subscribe(observer: T)
}