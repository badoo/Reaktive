package com.badoo.reaktive.completable

import com.badoo.reaktive.base.ErrorCallback

/**
 * Callbacks for [Completable] source.
 * See [Completable] and [ErrorCallback] for more information.
 */
interface CompletableCallbacks : ErrorCallback {

    /**
     * Notifies the host (typically an observer) about completion
     */
    fun onComplete()
}