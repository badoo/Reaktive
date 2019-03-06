package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

internal inline fun disposable(crossinline onDispose: () -> Unit = {}): Disposable =
    object : Disposable {
        private val lock = newLock()

        @Suppress("ObjectPropertyName") // backing property
        private var _isDisposed = false
        override val isDisposed: Boolean get() = lock.synchronized { _isDisposed }

        override fun dispose() {
            if (!_isDisposed) {
                lock.synchronized {
                    if (_isDisposed) {
                        return
                    }
                    _isDisposed = true
                }

                onDispose()
            }
        }
    }