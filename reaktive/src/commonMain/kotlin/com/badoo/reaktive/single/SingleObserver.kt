package com.badoo.reaktive.single

import com.badoo.reaktive.base.Observer

/**
 * Represents an [Observer] of [Single] source.
 * See [Observer] and [SingleCallbacks] for more information.
 */
interface SingleObserver<in T> : Observer, SingleCallbacks<T>