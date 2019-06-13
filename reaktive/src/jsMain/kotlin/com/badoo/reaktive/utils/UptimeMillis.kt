package com.badoo.reaktive.utils

import kotlin.browser.window

internal actual val uptimeMillis: Long get() = if (isWindowDefined) window.performance.now().toLong() else hrTimeMillis

private external val process: dynamic

private val isWindowDefined: Boolean = jsTypeOf(window) != "undefined"

private val hrTimeMillis: Long
    get() {
        val t = process.hrtime()

        return ((t[0] as Int) * 1000 + (t[1] as Int) / 1000000).toLong()
    }
