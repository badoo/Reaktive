package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.CompletableObserver

/**
 * Represents [Observer] that in addition to [CompletableObserver] can accept completion with value
 */
interface MaybeObserver<in T> : CompletableObserver {

    /**
     * Notifies the [Observer] about completion with value, no other methods must be called after this method
     *
     * @param value the value
     */
    fun onSuccess(value: T)
}