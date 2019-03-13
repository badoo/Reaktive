package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.Disposable

/**
 * Base interface for emitting signals from various [Source]s.
 * Note that its methods must be called synchronously, never concurrently.
 * Unlike [Observer]s it is safe to call any methods of [Emitter] in any order.
 */
interface Emitter {

    /**
     * Signals an exception.
     * It's safe to call any other methods of the [Emitter] after error though it has no effect.
     *
     * @param error the Throwable to signal
     */
    fun onError(error: Throwable)

    /**
     * Sets a [Disposable] on this emitter, any previous [Disposable]
     * will be replaced.
     *
     * @param disposable the [Disposable]
     */
    fun setDisposable(disposable: Disposable)
}