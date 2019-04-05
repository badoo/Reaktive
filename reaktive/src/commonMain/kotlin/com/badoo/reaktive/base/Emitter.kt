package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.Disposable

/**
 * Base interface for emitting signals from various sources.
 * Note that emitters' methods must be called synchronously, never concurrently.
 * Unlike [Observer] it is safe to call any methods of [Emitter] in any order.
 */
interface Emitter {

    /**
     * Sets a [Disposable] on this emitter, any previous [Disposable]
     * will be replaced.
     *
     * @param disposable the [Disposable]
     */
    fun setDisposable(disposable: Disposable)
}