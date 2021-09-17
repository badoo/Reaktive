package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.NANOS_IN_SECOND
import platform.posix.timespec

internal actual operator fun timespec.plusAssign(nanos: Long) {
    tv_sec += nanos / NANOS_IN_SECOND
    tv_nsec += nanos % NANOS_IN_SECOND
    if (tv_nsec >= NANOS_IN_SECOND) {
        tv_sec += 1
        tv_nsec -= NANOS_IN_SECOND
    }
}
