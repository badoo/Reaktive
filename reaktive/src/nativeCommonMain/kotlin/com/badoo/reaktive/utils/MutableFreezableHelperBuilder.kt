package com.badoo.reaktive.utils

@Suppress("FunctionName")
internal inline fun <T : Any, M : T, F : T> MutableFreezableHelper(
    crossinline mutableFactory: () -> M,
    crossinline freezableFactory: (mutable: M?) -> F
): MutableFreezableHelper<T, M, F> =
    object : MutableFreezableHelper<T, M, F>() {
        override fun createMutable(): M = mutableFactory()

        override fun createFreezable(mutable: M?): F = freezableFactory(mutable)
    }
