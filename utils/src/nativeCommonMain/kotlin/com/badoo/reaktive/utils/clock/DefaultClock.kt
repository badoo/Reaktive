package com.badoo.reaktive.utils.clock

import kotlin.system.getTimeMillis
import kotlin.system.getTimeNanos

actual object DefaultClock : Clock {

    override val uptimeMillis: Long get() = getTimeMillis()

    override val uptimeNanos: Long get() = getTimeNanos()
}
