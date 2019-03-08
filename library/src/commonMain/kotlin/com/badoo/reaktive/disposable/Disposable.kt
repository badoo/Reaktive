package com.badoo.reaktive.disposable

/**
 * Represents a disposable resource
 */
interface Disposable {

    /**
     * Checks whether this resource is disposed or not
     *
     * @return true if this resource is disposed, false otherwise
     */
    val isDisposed: Boolean

    /**
     * Disposes this resource
     */
    fun dispose()
}