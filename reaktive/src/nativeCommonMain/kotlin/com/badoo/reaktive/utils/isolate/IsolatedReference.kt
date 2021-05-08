package com.badoo.reaktive.utils.isolate

import kotlin.native.concurrent.FreezableAtomicReference
import kotlin.native.concurrent.WorkerBoundReference

internal actual class IsolatedReference<out T : Any> actual constructor(value: T) : SharedReference<T> {

    private val ref: FreezableAtomicReference<WorkerBoundReference<T>?> = FreezableAtomicReference(WorkerBoundReference(value))

    override val isDisposed: Boolean get() = ref.value == null

    override fun dispose() {
        ref.value = null
    }

    actual override fun getOrThrow(): T? = ref.value?.value
}
