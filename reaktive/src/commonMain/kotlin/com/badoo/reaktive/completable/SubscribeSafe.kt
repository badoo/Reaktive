package com.badoo.reaktive.completable

import com.badoo.reaktive.utils.handleSourceError

internal fun Completable.subscribeSafe(observer: CompletableObserver, onError: ((Throwable) -> Unit)? = null) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        handleSourceError(e, onError)
    }
}