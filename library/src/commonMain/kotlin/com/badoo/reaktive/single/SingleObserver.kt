package com.badoo.reaktive.single

import com.badoo.reaktive.base.Subscribable

/**
 * Represents an observer of Single source.
 * See [Subscribable] and [SingleCallbacks] for more information.
 */
interface SingleObserver<in T> : Subscribable, SingleCallbacks<T>