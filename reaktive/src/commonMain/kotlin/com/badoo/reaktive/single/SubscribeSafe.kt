package com.badoo.reaktive.single

import com.badoo.reaktive.utils.handleSourceError

internal fun <T> Single<T>.subscribeSafe(observer: SingleObserver<T>) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        handleSourceError(e)
    }
}