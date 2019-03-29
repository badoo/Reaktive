package com.badoo.reaktive.completable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer

/**
 * Callbacks for [Completable] source.
 * See [Completable] and [ErrorCallback] for more information.
 */
interface CompletableCallbacks : ErrorCallback {

    /**
     * Notifies the host (typically an [Observer]) about completion
     */
    fun onComplete()
}