package com.badoo.reaktive.utils.clock

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.time.TimeSource.Monotonic.ValueTimeMark

@InternalReaktiveApi
interface Clock {

    val uptime: ValueTimeMark
}
