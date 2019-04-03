package com.badoo.reaktive.observable

import com.badoo.reaktive.utils.handleSourceError

internal fun <T> Observable<T>.subscribeSafe(observer: ObservableObserver<T>, onError: ((Throwable) -> Unit)? = null) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        handleSourceError(e, onError)
    }
}