package com.badoo.reaktive.disposable

import kotlin.native.concurrent.AtomicInt

@Suppress("FunctionName")
actual inline fun Disposable(crossinline onDispose: () -> Unit): Disposable =
    object : Disposable {
        @Suppress("ObjectPropertyName") // Backing property
        private var _isDisposed = AtomicInt(0)
        override val isDisposed: Boolean get() = _isDisposed.value != 0

        override fun dispose() {
            if (_isDisposed.compareAndSet(0, 1)) {
                onDispose()
            }
        }
    }

@Suppress("FunctionName")
actual fun Disposable(): Disposable = SimpleDisposable()

private class SimpleDisposable : Disposable {
    private var _isDisposed = AtomicInt(0)
    override val isDisposed: Boolean get() = _isDisposed.value != 0

    override fun dispose() {
        _isDisposed.value = 1
    }
}
