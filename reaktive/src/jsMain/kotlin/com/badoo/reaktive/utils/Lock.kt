package com.badoo.reaktive.utils

internal actual class Lock {

    actual fun acquire() {
        // no-op
    }

    actual fun release() {
        // no-op
    }

    actual fun destroy() {
        // no-op
    }

    actual fun newCondition(): Condition = condition

    private companion object {
        private val condition =
            object : Condition {
                override fun await(timeoutNanos: Long) {
                    // no-op
                }

                override fun signal() {
                    // no-op
                }

                override fun destroy() {
                    // no-op
                }
            }
    }
}