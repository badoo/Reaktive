package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.atomicreference.AtomicReference

internal inline fun disposable(crossinline onDispose: () -> Unit = {}): Disposable =
    object : Disposable {
        @Suppress("ObjectPropertyName") // Backing property
        private var _isDisposed = AtomicReference(false)
        override val isDisposed: Boolean get() = _isDisposed.value

        override fun dispose() {
            if (_isDisposed.compareAndSet(false, true)) {
                onDispose()
            }
        }
    }