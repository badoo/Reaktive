package com.badoo.reaktive.disposable

import kotlin.concurrent.AtomicInt

actual inline fun Disposable(crossinline onDispose: () -> Unit): Disposable =
    object : Disposable {
        private var _isDisposed = AtomicInt(0)
        override val isDisposed: Boolean get() = _isDisposed.value != 0

        override fun dispose() {
            if (_isDisposed.compareAndSet(0, 1)) {
                onDispose()
            }
        }
    }

actual fun Disposable(): Disposable = SimpleDisposable()

private class SimpleDisposable : Disposable {
    private var _isDisposed = AtomicInt(0)
    override val isDisposed: Boolean get() = _isDisposed.value != 0

    override fun dispose() {
        _isDisposed.value = 1
    }
}
