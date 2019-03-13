package com.badoo.reaktive.observable

internal fun <T> Observable<T>.subscribeSafe(observer: ObservableObserver<T>) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        observer.onError(e)
    }
}