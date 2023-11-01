package com.badoo.reaktive.utils.clock

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.time.TimeSource
import kotlin.time.TimeSource.Monotonic.ValueTimeMark

@InternalReaktiveApi
object DefaultClock : Clock {

    override val uptime: ValueTimeMark get() = TimeSource.Monotonic.markNow()
}
