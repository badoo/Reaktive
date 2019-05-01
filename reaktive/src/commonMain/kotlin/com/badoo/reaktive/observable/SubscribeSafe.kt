package com.badoo.reaktive.observable

import com.badoo.reaktive.utils.handleSourceError

internal fun <T> Observable<T>.subscribeSafe(observer: ObservableObserver<T>) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        handleSourceError(e, observer::onError)
    }
}