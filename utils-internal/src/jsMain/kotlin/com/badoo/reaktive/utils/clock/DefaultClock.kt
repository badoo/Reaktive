package com.badoo.reaktive.utils.clock

import com.badoo.reaktive.utils.InternalReaktiveApi
import com.badoo.reaktive.utils.MILLIS_IN_SECOND
import com.badoo.reaktive.utils.NANOS_IN_MILLI
import com.badoo.reaktive.utils.NANOS_IN_SECOND
import kotlinx.browser.window

private external val process: dynamic

@InternalReaktiveApi
actual object DefaultClock : Clock {

    override val uptimeMillis: Long
        get() = if (isWindowDefined) window.performance.now().toLong() else hrTimeMillis

    override val uptimeNanos: Long
        get() = if (isWindowDefined) window.performance.now().toLong() * NANOS_IN_MILLI else hrTimeNanos

    private val isWindowDefined: Boolean = jsTypeOf(window) != "undefined"

    private val hrTimeMillis: Long
        get() {
            val t = process.hrtime()

            return ((t[0] as Int) * MILLIS_IN_SECOND + (t[1] as Int) / NANOS_IN_MILLI)
        }
    private val hrTimeNanos: Long
        get() {
            val t = process.hrtime()

            return (t[0] as Int) * NANOS_IN_SECOND + t[1] as Int
        }
}
