package com.badoo.reaktive.utils.isolate

import kotlin.native.concurrent.WorkerBoundReference

internal actual class IsolatedReference<out T : Any> actual constructor(value: T) : SharedReference<T> {

    private val ref = WorkerBoundReference(value)

    override val value: T get() = ref.value
}
