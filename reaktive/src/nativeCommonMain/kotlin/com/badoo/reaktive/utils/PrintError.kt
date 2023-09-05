package com.badoo.reaktive.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fprintf
import platform.posix.stderr

internal actual fun printError(error: Any?) {
    @OptIn(ExperimentalForeignApi::class)
    fprintf(stderr, error.toString())
}
