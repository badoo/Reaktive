package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.completable.CompletableCallbacks

/**
 * Callbacks for [Maybe] source
 * See [Maybe], [SuccessCallback] and [CompletableCallbacks] for more information.
 */
interface MaybeCallbacks<in T> : SuccessCallback<T>, CompletableCallbacks