package com.badoo.reaktive.completable

/**
 * Represents an action that either completes or produces an error
 */
interface Completable {

    /**
     * Subscribes the specified [CompletableObserver] to this [Completable]
     *
     * @param observer the [CompletableObserver] to be subscribed
     */
    fun subscribe(observer: CompletableObserver)
}