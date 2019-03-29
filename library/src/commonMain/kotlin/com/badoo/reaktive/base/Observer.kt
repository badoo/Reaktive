package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.Disposable

/**
 * Base interface for event observers.
 * When an [Observer] is subscribed to a source through its subscribe method,
 * the source calls [onSubscribe] method with a [Disposable] that allows
 * disposing the source at any time.
 * All methods must be called synchronously, never concurrently.
 */
interface Observer {

    /**
     * Provides the [Observer] with a [Disposable]. This method must be called before any other methods.
     *
     * @param disposable the [Disposable] that can be used to cancel the source
     */
    fun onSubscribe(disposable: Disposable)
}