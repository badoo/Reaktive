package com.badoo.reaktive.utils

import kotlin.browser.window

external fun setTimeout(handler: dynamic, timeout: Int = definedExternally, vararg arguments: Any?): Int
external fun setInterval(handler: dynamic, timeout: Int = definedExternally, vararg arguments: Any?): Int
external fun clearTimeout(handle: Int = definedExternally)
external fun clearInterval(handle: Int = definedExternally)

object Timers {
    fun setTimeout(delayMillis: Long, task: () -> Unit, vararg arguments: Any?): Int {
        return try {
            window.setTimeout(task, delayMillis.toInt(), arguments)
        } catch (ex: dynamic) {
            setTimeout(task, delayMillis.toInt(), arguments)
        }
    }

    fun setInterval(delayMillis: Long, task: () -> Unit, vararg arguments: Any?): Int {
        return try {
            window.setInterval(task, delayMillis.toInt(), arguments)
        } catch (ex: dynamic) {
            setInterval(task, delayMillis.toInt(), arguments)
        }
    }

    fun clearTimeout(interval: Long) {
        try {
            window.clearTimeout(interval.toInt())
        } catch (ex: dynamic) {
            clearTimeout(interval.toInt())
        }
    }

    fun clearInterval(interval: Long) {
        try {
            window.clearInterval(interval.toInt())
        } catch (ex: dynamic) {
            clearInterval(interval.toInt())
        }
    }
}
