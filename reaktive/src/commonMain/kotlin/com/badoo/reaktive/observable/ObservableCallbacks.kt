package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.completable.CompletableCallbacks

/**
 * Callbacks for [Observable] source.
 * See [ValueCallback] and [CompletableCallbacks] for more information.
 */
interface ObservableCallbacks<in T> : ValueCallback<T>, CompletableCallbacks
