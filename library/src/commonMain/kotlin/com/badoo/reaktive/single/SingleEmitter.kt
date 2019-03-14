package com.badoo.reaktive.single

import com.badoo.reaktive.base.Emitter

/**
 * Represents [Emitter] that signals completion with value
 */
interface SingleEmitter<in T> : Emitter {

    /**
     * Signals completion with value
     *
     * @param value the value
     */
    fun onSuccess(value: T)
}