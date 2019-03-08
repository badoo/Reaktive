package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.Disposable

/**
 * Base interface for receivers of events from [Source]s.
 * When an [Observer] is subscribed to a [Source] through its [Source.subscribe] method,
 * the [Source] calls [onSubscribe] method with a [Disposable] that allows
 * disposing the sequence at any time.
 * All methods must be called synchronously, never concurrently.
 */
interface Observer {

    /**
     * Provides the [Observer] with a [Disposable]
     *
     * @param disposable the [Disposable] that can be used to cancel the [Source]
     */
    fun onSubscribe(disposable: Disposable)

    /**
     * Notifies the [Observer] about error, no other methods must be called after this method
     *
     * @param error the exception
     */
    fun onError(error: Throwable)
}