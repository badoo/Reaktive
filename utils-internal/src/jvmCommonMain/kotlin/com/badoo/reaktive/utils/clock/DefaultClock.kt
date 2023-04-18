package com.badoo.reaktive.utils.clock

import com.badoo.reaktive.utils.InternalReaktiveApi
import java.util.concurrent.TimeUnit

@InternalReaktiveApi
actual object DefaultClock : Clock {

    override val uptimeMillis: Long get() = TimeUnit.NANOSECONDS.toMillis(System.nanoTime())

    override val uptimeNanos: Long get() = System.nanoTime()
}
