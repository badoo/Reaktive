package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Emitter
import com.badoo.reaktive.completable.CompletableEmitter

/**
 * Represents [Emitter] that in addition to [CompletableEmitter] can signal completion with value
 */
interface MaybeEmitter<in T> : CompletableEmitter {

    /**
     * Signals completion with value
     *
     * @param value the value
     */
    fun onSuccess(value: T)
}