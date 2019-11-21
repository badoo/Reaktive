package com.badoo.reaktive.utils.lock

actual class Lock {

    actual fun acquire() {
        // no-op
    }

    actual fun release() {
        // no-op
    }

    actual fun destroy() {
        // no-op
    }

    actual fun newCondition(): Condition {
        throw IllegalStateException("Condition is not supported in JavaScript")
    }
}
