package com.badoo.reaktive.completable

/**
 * Represents a source that either completes or produces an error.
 * See [CompletableCallbacks] for more information.
 */
interface Completable {

    /**
     * Subscribes the specified [CompletableObserver] to this [Completable]
     *
     * @param observer the [CompletableObserver] to be subscribed
     */
    fun subscribe(observer: CompletableObserver)
}