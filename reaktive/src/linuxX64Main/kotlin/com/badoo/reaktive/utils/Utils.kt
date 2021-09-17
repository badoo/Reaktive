package com.badoo.reaktive.utils

import platform.posix._SC_NPROCESSORS_ONLN
import platform.posix.sysconf

internal actual fun processorCount(): Int = sysconf(_SC_NPROCESSORS_ONLN).toInt()

internal actual fun usleep(millis: UInt) {
    platform.posix.usleep(millis)
}
