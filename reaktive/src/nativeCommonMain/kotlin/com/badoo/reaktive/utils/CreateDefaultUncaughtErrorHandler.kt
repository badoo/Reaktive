package com.badoo.reaktive.utils

import platform.posix.EXIT_FAILURE
import platform.posix.fprintf
import platform.posix.stderr
import kotlin.system.exitProcess

internal actual fun createDefaultUncaughtErrorHandler(): (Throwable) -> Unit =
    { e ->
        printError("Uncaught exception: $e")
        e.printStackTrace()
        exitProcess(EXIT_FAILURE)
    }
