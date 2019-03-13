package com.arkivanov.rxkotlin.maybe

internal fun <T> Maybe<T>.subscribeSafe(observer: MaybeObserver<T>) {
    try {
        subscribe(observer)
    } catch (e: Throwable) {
        observer.onError(e)
    }
}