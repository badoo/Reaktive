package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Subscribable

/**
 * Represents an observer of Observable source.
 * See [Subscribable] and [ObservableCallbacks] for more information.
 */
interface ObservableObserver<in T> : Subscribable, ObservableCallbacks<T>