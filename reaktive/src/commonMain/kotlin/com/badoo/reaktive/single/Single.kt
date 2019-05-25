package com.badoo.reaktive.single

import com.badoo.reaktive.base.Source

/**
 * Represents a [Source] that can complete with a value or produce an error.
 * See [Source] and [SingleCallbacks] for more information.
 */
interface Single<out T> : Source<SingleObserver<T>>