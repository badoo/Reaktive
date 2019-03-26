package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback

/**
 * Callbacks for [Single] source.
 * See [Single], [SingleCallbacks] and [ErrorCallback] for more information.
 */
interface SingleCallbacks<in T> : ErrorCallback {

    fun onSuccess(value: T)
}