package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Observer

/**
 * Represents [Observer] of completable source
 */
interface CompletableObserver : Observer {

    /**
     * Notifies the [Observer] about completion, no other methods must be called after this method
     */
    fun onComplete()
}