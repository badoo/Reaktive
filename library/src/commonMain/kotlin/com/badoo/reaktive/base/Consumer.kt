package com.badoo.reaktive.base

/**
 * Interface for consumers
 */
interface Consumer<in T> {

    /**
     * Consumes the given value
     *
     * @param value the value
     */
    fun accept(value: T)
}