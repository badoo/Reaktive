package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.NANOS_IN_MICRO
import com.badoo.reaktive.utils.NANOS_IN_SECOND
import platform.posix.timespec
import platform.posix.timeval

internal actual operator fun timespec.plusAssign(nanos: Long) {
    tv_sec += (nanos / NANOS_IN_SECOND).toInt()
    tv_nsec += (nanos % NANOS_IN_SECOND).toInt()
    if (tv_nsec >= NANOS_IN_SECOND) {
        tv_sec += 1
        tv_nsec -= NANOS_IN_SECOND.toInt()
    }
}

internal actual fun timespec.set(t: timeval) {
    tv_sec = t.tv_sec
    tv_nsec = (t.tv_usec * NANOS_IN_MICRO).toInt()
}
