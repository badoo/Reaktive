package com.badoo.reaktive.maybe

import com.badoo.reaktive.utils.handleSourceError

internal fun <T> Maybe<T>.subscribeSafe(observer: MaybeObserver<T>) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        handleSourceError(e, observer::onError)
    }
}