package com.badoo.reaktive.utils

import platform.posix.EXIT_FAILURE
import kotlin.system.exitProcess

internal actual fun createDefaultUncaughtErrorHandler(): (Throwable) -> Unit = { e ->
    printError("Uncaught exception: $e")
    e.printStackTrace()
    exitProcess(EXIT_FAILURE)
}
