package com.badoo.reaktive.utils.clock

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

@InternalReaktiveApi
actual object DefaultClock : Clock {

    override val uptime: Duration
        get() = System.nanoTime().nanoseconds
}
