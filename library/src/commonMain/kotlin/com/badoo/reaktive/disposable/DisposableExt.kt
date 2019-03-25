package com.badoo.reaktive.disposable

internal inline fun Disposable.wrap(
    crossinline onBeforeDispose: () -> Unit = {},
    crossinline onAfterDispose: () -> Unit = {}
): Disposable =
    object : Disposable by this {
        override fun dispose() {
            onBeforeDispose()
            this@wrap.dispose()
            onAfterDispose()
        }
    }