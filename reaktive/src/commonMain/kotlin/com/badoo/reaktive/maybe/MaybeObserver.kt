package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer

/**
 * Represents an [Observer] of [Maybe] source.
 * See [Observer] and [MaybeCallbacks] for more information.
 */
interface MaybeObserver<in T> : Observer, MaybeCallbacks<T>
