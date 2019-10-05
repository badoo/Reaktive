package com.badoo.reaktive.utils.clock

import kotlin.browser.window

private external val process: dynamic

actual object DefaultClock : Clock {

    override val uptimeMillis: Long
        get() = if (isWindowDefined) window.performance.now().toLong() else hrTimeMillis

    override val uptimeNanos: Long
        get() = if (isWindowDefined) window.performance.now().toLong() * 1_000_000L else hrTimeNanos

    private val isWindowDefined: Boolean = jsTypeOf(window) != "undefined"

    private val hrTimeMillis: Long
        get() {
            val t = process.hrtime()

            return ((t[0] as Int) * 1_000 + (t[1] as Int) / 1_000_000).toLong()
        }
    private val hrTimeNanos: Long
        get() {
            val t = process.hrtime()

            return ((t[0] as Int) * 1_000_000_000 + t[1] as Int).toLong()
        }
}