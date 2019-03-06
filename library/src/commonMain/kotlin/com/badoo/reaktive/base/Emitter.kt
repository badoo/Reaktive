package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.Disposable

/**
 * Base interface for emitting signals from various [Source]s.
 * Note that its methods must be called synchronously, never concurrently.
 */
interface Emitter {

    /**
     * Signal an exception
     *
     * @param e the Throwable to signal
     */
    fun onError(e: Throwable)

    /**
     * Sets a [Disposable] on this emitter, any previous [Disposable]
     * will be replaced.
     *
     * @param disposable the [Disposable]
     */
    fun setDisposable(disposable: Disposable)
}