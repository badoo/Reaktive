package com.badoo.reaktive.utils

import platform.posix.fprintf
import platform.posix.stderr

internal actual fun printError(error: Any?) {
    if (isPrintErrorEnabled) {
        fprintf(stderr, error.toString())
    }
}