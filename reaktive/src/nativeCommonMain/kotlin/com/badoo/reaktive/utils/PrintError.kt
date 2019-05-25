package com.badoo.reaktive.utils

import platform.posix.fprintf
import platform.posix.stderr

internal actual fun printError(error: Any?) {
    fprintf(stderr, error.toString())
}