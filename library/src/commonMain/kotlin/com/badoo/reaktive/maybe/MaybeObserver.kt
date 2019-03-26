package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Subscribable

/**
 * Represents an observer of Maybe source.
 * See [Subscribable] and [MaybeCallbacks] for more information.
 */
interface MaybeObserver<in T> : Subscribable, MaybeCallbacks<T>