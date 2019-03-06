package com.badoo.reaktive.base

internal fun <T : Observer> Source<T>.subscribeSafe(observer: T) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        observer.onError(e)
    }
}