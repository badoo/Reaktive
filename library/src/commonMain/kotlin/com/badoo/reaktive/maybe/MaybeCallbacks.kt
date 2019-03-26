package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.single.SingleCallbacks

/**
 * Callbacks for [Maybe] source
 * See [Maybe], [CompletableCallbacks] and [SingleCallbacks] for more information.
 */
interface MaybeCallbacks<in T> : CompletableCallbacks, SingleCallbacks<T>