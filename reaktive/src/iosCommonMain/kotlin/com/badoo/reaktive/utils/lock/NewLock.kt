package com.badoo.reaktive.utils.lock

internal actual fun newLock(): Lock =
    // TODO Implement iOS lock when native multithreading is ready
    object : Lock {
        override fun acquire() {
            // no-op
        }

        override fun release() {
            // no-op
        }
    }