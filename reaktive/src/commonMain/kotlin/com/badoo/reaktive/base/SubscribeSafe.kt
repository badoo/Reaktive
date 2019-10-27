package com.badoo.reaktive.base

import com.badoo.reaktive.utils.handleSourceError

internal fun <T> Source<T>.subscribeSafe(observer: T) where T : Observer, T : ErrorCallback {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        handleSourceError(e, observer::onError)
    }
}
