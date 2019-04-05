package com.badoo.reaktive.observable

/**
 * Represents a source that begins with producing values (optional) and then completes or produces an exception.
 * See [ObservableCallbacks] for more information.
 */
interface Observable<out T> {

    /**
     * Subscribes the specified [ObservableObserver] to this [Observable]
     *
     * @param observer the [ObservableObserver] to be subscribed
     */
    fun subscribe(observer: ObservableObserver<T>)
}