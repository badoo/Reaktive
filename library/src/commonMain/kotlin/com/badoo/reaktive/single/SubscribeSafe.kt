package com.badoo.reaktive.single

internal fun <T> Single<T>.subscribeSafe(observer: SingleObserver<T>) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        observer.onError(e)
    }
}