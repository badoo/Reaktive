package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Emitter

/**
 * Represents an emitter for Observable source.
 * See [Emitter] and [ObservableCallbacks] for more information.
 */
interface ObservableEmitter<in T> : Emitter, ObservableCallbacks<T>
