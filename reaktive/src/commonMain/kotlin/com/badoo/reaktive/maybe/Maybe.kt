package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Source

/**
 * Represents a [Source] that can complete with or without a value or produce an error.
 *
 * There are a number of factory functions available, their names all begin with `maybe*(...)`.
 *
 * See [Source] and [MaybeCallbacks] for more information.
 */
interface Maybe<out T> : Source<MaybeObserver<T>>
