package com.badoo.reaktive.completable

import com.badoo.reaktive.utils.handleSourceError

internal fun Completable.subscribeSafe(observer: CompletableObserver) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        handleSourceError(e, observer::onError)
    }
}