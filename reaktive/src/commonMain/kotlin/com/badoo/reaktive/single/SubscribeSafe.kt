package com.badoo.reaktive.single

import com.badoo.reaktive.utils.handleSourceError

internal fun <T> Single<T>.subscribeSafe(observer: SingleObserver<T>, onError: ((Throwable) -> Unit)? = null) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        handleSourceError(e, onError)
    }
}