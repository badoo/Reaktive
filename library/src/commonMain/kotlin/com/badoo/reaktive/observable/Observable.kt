package com.badoo.reaktive.observable

/**
 * Produces stream of values and completes at the end
 */
interface Observable<out T> {

    /**
     * Subscribes the specified [ObservableObserver] to this [Observable]
     *
     * @param observer the [ObservableObserver] to be subscribed
     */
    fun subscribe(observer: ObservableObserver<T>)
}