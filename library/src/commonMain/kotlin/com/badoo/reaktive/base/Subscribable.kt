package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.Disposable

/**
 * Provides capability of being subscribed
 */
interface Subscribable {

    /**
     * Called at the beginning of subscription
     */
    fun onSubscribe(disposable: Disposable)
}