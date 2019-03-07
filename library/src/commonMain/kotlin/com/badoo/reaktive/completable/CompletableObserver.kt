package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.Source

/**
 * Represents [Observer] of completable [Source]
 */
interface CompletableObserver : Observer {

    /**
     * Notifies the [Observer] about completion, no other methods must be called after this method
     */
    fun onComplete()
}