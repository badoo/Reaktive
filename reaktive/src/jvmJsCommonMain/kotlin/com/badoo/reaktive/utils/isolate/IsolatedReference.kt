package com.badoo.reaktive.utils.isolate

internal actual class IsolatedReference<out T : Any> actual constructor(
    override val value: T
) : SharedReference<T>
