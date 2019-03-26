package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Emitter

/**
 * Represents an emitter for Maybe source.
 * See [Emitter] and [MaybeCallbacks] for more information.
 */
interface MaybeEmitter<in T> : Emitter, MaybeCallbacks<T>