package com.badoo.reaktive.completable

internal fun Completable.subscribeSafe(observer: CompletableObserver) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        observer.onError(e)
    }
}