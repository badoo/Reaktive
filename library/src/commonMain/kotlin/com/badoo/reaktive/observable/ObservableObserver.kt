package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Observer

/**
 * Represents an [Observer] of [Observable] source.
 * See [Observer] and [ObservableCallbacks] for more information.
 */
interface ObservableObserver<in T> : Observer, ObservableCallbacks<T>