package com.badoo.reaktive.utils

import platform.posix.abort

internal actual fun createDefaultUncaughtErrorHandler(): (Throwable) -> Unit = { e ->
    printError("Uncaught exception: $e")
    e.printStackTrace()
    abort()
}
