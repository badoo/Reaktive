package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
actual class Lock {

    actual fun acquire() {
        // no-op
    }

    actual fun release() {
        // no-op
    }

    actual fun newCondition(): Condition {
        error("Condition is not supported in JavaScript")
    }
}
