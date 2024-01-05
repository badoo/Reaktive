package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.time.Duration

@InternalReaktiveApi
actual open class ConditionLock {

    actual fun lock() {
        // no-op
    }

    actual fun unlock() {
        // no-op
    }

    actual fun await(timeout: Duration): Duration {
        error("Not supported on JS")
    }

    actual fun signal() {
        error("Not supported on JS")
    }
}
