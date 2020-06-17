package com.badoo.reaktive.utils

import kotlin.browser.window

external fun setTimeout(handler: dynamic, timeout: Int = definedExternally, vararg arguments: Any?): Int
external fun setInterval(handler: dynamic, timeout: Int = definedExternally, vararg arguments: Any?): Int
external fun clearTimeout(handle: Int = definedExternally)
external fun clearInterval(handle: Int = definedExternally)


object Timers {
    // TODO Verbose, just using for POC, remove
    private val isWindowDefined: Boolean = jsTypeOf(window) != "undefined"

    fun setTimeout(delayMillis: Long, task: () -> Unit, vararg arguments: Any?): Int =
        if (isWindowDefined) window.setTimeout(task, delayMillis.toInt(), arguments) else setTimeout(task, delayMillis.toInt(), arguments)

    fun setInterval(delayMillis: Long, task: () -> Unit, vararg arguments: Any?): Int =
        if (isWindowDefined) window.setInterval(task, delayMillis.toInt(), arguments) else setInterval(task, delayMillis.toInt(), arguments)

    fun clearTimeout(interval: Long) {
        if (isWindowDefined) window.clearTimeout(interval.toInt()) else clearTimeout(interval.toInt())
    }

    fun clearInterval(interval: Long) {
        if (isWindowDefined) window.clearInterval(interval.toInt()) else clearInterval(interval.toInt())
    }

}
