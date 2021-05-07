package com.badoo.reaktive.utils.isolate

internal actual class IsolatedReference<out T : Any> actual constructor(
    private val value: T
) : SharedReference<T> {

    override var isDisposed: Boolean = false

    override fun dispose() {
        isDisposed = true
    }

    actual override fun getOrThrow(): T? = value
}
