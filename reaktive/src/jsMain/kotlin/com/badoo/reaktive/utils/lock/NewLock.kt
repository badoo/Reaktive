package com.badoo.reaktive.utils.lock

internal actual fun newLock(): Lock =
    object : Lock {
        override fun acquire() {
            // no-op
        }

        override fun release() {
            // no-op
        }
    }
