package com.badoo.reaktive.utils.clock

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlinx.browser.window
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

private external val process: dynamic

@InternalReaktiveApi
actual object DefaultClock : Clock {

    private val isWindowDefined = jsTypeOf(window) != "undefined"

    override val uptime: Duration
        get() =
            if (isWindowDefined) {
                window.performance.now().milliseconds
            } else {
                val t = process.hrtime().unsafeCast<IntArray>()
                t[0].seconds + t[1].nanoseconds
            }
}
