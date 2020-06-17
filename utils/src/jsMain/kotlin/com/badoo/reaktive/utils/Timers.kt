package com.badoo.reaktive.utils

import kotlin.browser.window

import com.badoo.reaktive.utils.timer.external.setTimeout as setTimeoutExternal
import com.badoo.reaktive.utils.timer.external.setInterval as setIntervalExternal
import com.badoo.reaktive.utils.timer.external.clearTimeout as clearTimeoutExternal
import com.badoo.reaktive.utils.timer.external.clearInterval as clearIntervalExternal

fun setTimeout(task: () -> Unit, delayMillis: Int): Int {
    return try {
        window.setTimeout(task, delayMillis)
    } catch (ex: dynamic) {
        setTimeoutExternal(task, delayMillis)
    }
}

fun setInterval(task: () -> Unit, delayMillis: Int): Int {
    return try {
        window.setInterval(task, delayMillis)
    } catch (ex: dynamic) {
        setIntervalExternal(task, delayMillis)
    }
}

fun clearTimeout(interval: Int) {
    try {
        window.clearTimeout(interval)
    } catch (ex: dynamic) {
        clearTimeoutExternal(interval)
    }
}

fun clearInterval(interval: Int) {
    try {
        window.clearInterval(interval)
    } catch (ex: dynamic) {
        clearIntervalExternal(interval)
    }
}
