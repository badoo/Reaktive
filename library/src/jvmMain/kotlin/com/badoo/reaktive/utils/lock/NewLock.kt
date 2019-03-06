package com.badoo.reaktive.utils.lock

import java.util.concurrent.locks.ReentrantLock

internal actual fun newLock(): Lock =
    object : Lock {
        private val delegate = ReentrantLock()

        override fun acquire() {
            delegate.lock()
        }

        override fun release() {
            delegate.unlock()
        }
    }