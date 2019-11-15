package com.badoo.reaktive.base

import com.badoo.reaktive.utils.handleReaktiveError

internal fun <T> Source<T>.subscribeSafe(observer: T) where T : Observer, T : ErrorCallback {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        handleReaktiveError(e, observer::onError)
    }
}
