package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.Disposable

/**
 * Base interface for emitting signals from various sources.
 * Note that emitters' methods must be called synchronously, never concurrently.
 * Unlike [Observer] it is safe to call any methods of [Emitter] in any order.
 */
interface Emitter {

    val isDisposed: Boolean

    /**
     * Sets a [Disposable] on this emitter, any existing [Disposable]
     * will be replaced and disposed.
     *
     * @param disposable the [Disposable]
     */
    fun setDisposable(disposable: Disposable?)
}
