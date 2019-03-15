package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Emitter
import com.badoo.reaktive.completable.CompletableEmitter

/**
 * Represents [Emitter] that in addition to [CompletableEmitter] can signal values
 */
interface ObservableEmitter<in T> : CompletableEmitter {

    /**
     * Signals values
     *
     * @param value the value
     */
    fun onNext(value: T)
}