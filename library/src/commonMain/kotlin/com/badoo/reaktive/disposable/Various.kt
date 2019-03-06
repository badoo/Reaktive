package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

internal inline fun disposable(crossinline onDispose: () -> Unit = {}): Disposable =
    object : Disposable {
        private val lock = newLock()

        private var isDisposed_ = false
        override val isDisposed: Boolean get() = lock.synchronized { isDisposed_ }

        override fun dispose() {
            if (!isDisposed_) {
                lock.synchronized {
                    if (isDisposed_) {
                        return
                    }
                    isDisposed_ = true
                }

                onDispose()
            }
        }
    }