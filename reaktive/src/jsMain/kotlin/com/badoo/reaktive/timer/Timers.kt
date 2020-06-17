package com.badoo.reaktive.timer

import kotlin.browser.window
import com.badoo.reaktive.timer.external.clearInterval as clearIntervalExternal
import com.badoo.reaktive.timer.external.clearTimeout as clearTimeoutExternal
import com.badoo.reaktive.timer.external.setInterval as setIntervalExternal
import com.badoo.reaktive.timer.external.setTimeout as setTimeoutExternal

private val isWindowDefined: Boolean = jsTypeOf(window) != "undefined"

internal fun setTimeout(task: () -> Unit, delayMillis: Int): Int =
    if (isWindowDefined) window.setTimeout(task, delayMillis) else setTimeoutExternal(task, delayMillis)

internal fun setInterval(task: () -> Unit, delayMillis: Int): Int =
    if (isWindowDefined) window.setInterval(task, delayMillis) else setIntervalExternal(task, delayMillis)

internal fun clearTimeout(handler: Int) =
    if (isWindowDefined) window.clearTimeout(handler) else clearTimeoutExternal(handler)

internal fun clearInterval(handler: Int) =
    if (isWindowDefined) window.clearInterval(handler) else clearIntervalExternal(handler)
