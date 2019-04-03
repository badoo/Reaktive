package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks

/**
 * Callbacks for [Observable] source.
 * See [CompletableCallbacks] for more information.
 */
interface ObservableCallbacks<in T> : CompletableCallbacks {

    fun onNext(value: T)
}