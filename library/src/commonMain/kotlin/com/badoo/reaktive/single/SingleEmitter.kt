package com.badoo.reaktive.single

import com.badoo.reaktive.base.Emitter

/**
 * Represents an emitter for Single source.
 * See [Emitter] and [SingleCallbacks] for more information.
 */
interface SingleEmitter<in T> : Emitter, SingleCallbacks<T>