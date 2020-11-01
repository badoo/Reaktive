package com.badoo.reaktive.base

/**
 * Convenience method for [ValueCallback.onNext]
 */
operator fun <T> ValueCallback<T>.invoke(value: T) {
    onNext(value)
}
