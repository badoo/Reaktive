package com.badoo.reaktive.utils

internal actual fun printError(error: Any?) {
    when (error) {
        is Throwable -> error.printStackTrace()
        is String -> actualPrint(error)
        else -> actualPrint(error.toString())
    }
}

@Suppress("UnusedPrivateMember")
private fun actualPrint(str: String) {
    js("console.log(str)")
}
