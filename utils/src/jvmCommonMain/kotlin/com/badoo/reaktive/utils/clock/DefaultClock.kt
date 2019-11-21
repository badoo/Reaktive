package com.badoo.reaktive.utils.clock

import java.util.concurrent.TimeUnit

actual object DefaultClock : Clock {

    override val uptimeMillis: Long get() = TimeUnit.NANOSECONDS.toMillis(System.nanoTime())

    override val uptimeNanos: Long get() = System.nanoTime()
}
