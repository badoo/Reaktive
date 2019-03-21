package com.badoo.reaktive.single

import com.badoo.reaktive.base.Observer

/**
 * Represents [Observer] that accepts completion with value
 */
interface SingleObserver<in T> : Observer {

    /**
     * Notifies the [Observer] about completion with value, no other methods must be called after this method
     *
     * @param value the value
     */
    fun onSuccess(value: T)
}