package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Emitter

/**
 * Represents [Emitter] that can signal completion
 */
interface CompletableEmitter : Emitter {

    /**
     * Signals completion.
     * It's safe to call any other methods of the [Emitter] after completion though it has no effect.
     */
    fun onComplete()
}