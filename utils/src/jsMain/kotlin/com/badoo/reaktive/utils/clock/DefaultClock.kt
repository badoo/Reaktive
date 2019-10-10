package com.badoo.reaktive.utils.clock

import kotlin.browser.window

private external val process: dynamic

actual object DefaultClock : Clock {

    private const val SECOND_IN_NANOS = 1_000_000_000L
    private const val SECOND_IN_MICROS = 1_000_000L
    private const val SECOND_IN_MILLIS = 1_000L

    override val uptimeMillis: Long
        get() = if (isWindowDefined) window.performance.now().toLong() else hrTimeMillis

    override val uptimeNanos: Long
        get() = if (isWindowDefined) window.performance.now().toLong() * SECOND_IN_MICROS else hrTimeNanos

    private val isWindowDefined: Boolean = jsTypeOf(window) != "undefined"

    private val hrTimeMillis: Long
        get() {
            val t = process.hrtime()

            return (t[0] as Int) * SECOND_IN_MILLIS + (t[1] as Int) / SECOND_IN_MICROS
        }
    private val hrTimeNanos: Long
        get() {
            val t = process.hrtime()

            return (t[0] as Int) * SECOND_IN_NANOS + t[1] as Int
        }
}