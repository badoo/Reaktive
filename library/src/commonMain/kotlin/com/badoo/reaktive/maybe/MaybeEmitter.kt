package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Emitter
import com.badoo.reaktive.completable.CompletableEmitter
import com.badoo.reaktive.single.SingleEmitter

/**
 * Represents [Emitter] that acts as both [CompletableEmitter] and [SingleEmitter]
 */
interface MaybeEmitter<in T> : CompletableEmitter, SingleEmitter<T>