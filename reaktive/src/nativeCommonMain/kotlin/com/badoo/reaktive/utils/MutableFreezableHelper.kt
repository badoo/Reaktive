package com.badoo.reaktive.utils

import kotlin.native.concurrent.FreezableAtomicReference
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

internal abstract class MutableFreezableHelper<out T : Any, M : T, out F : T> {

    private val ref = FreezableAtomicReference<Holder<M, F>?>(null)

    val obj: T
        get() =
            when (val holder = ref.value) {
                is Holder.Mutable<M> -> if (holder.obj.isFrozen) initFreezable(mutable = holder.obj) else holder.obj
                is Holder.Freezable<F> -> holder.obj
                else -> if (ref.isFrozen) initFreezable() else initMutable()
            }

    private fun initMutable(): M =
        Holder.Mutable(createMutable())
            .also { ref.value = it }
            .obj

    private fun initFreezable(mutable: M? = null): F =
        Holder.Freezable(createFreezable(mutable))
            .freeze()
            .also { ref.value = it }
            .obj

    protected abstract fun createMutable(): M
    protected abstract fun createFreezable(mutable: M? = null): F

    private sealed class Holder<out M, out F> {
        class Mutable<out T>(val obj: T) : Holder<T, Nothing>()
        class Freezable<out T>(val obj: T) : Holder<Nothing, T>()
    }
}
