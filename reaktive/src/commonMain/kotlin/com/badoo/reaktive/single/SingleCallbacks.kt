package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback

/**
 * Callbacks for [Single] source.
 * See [Single], [SuccessCallback] and [ErrorCallback] for more information.
 */
interface SingleCallbacks<in T> : SuccessCallback<T>, ErrorCallback
