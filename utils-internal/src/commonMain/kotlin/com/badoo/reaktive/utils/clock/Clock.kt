package com.badoo.reaktive.utils.clock

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.time.Duration

@InternalReaktiveApi
interface Clock {

    val uptime: Duration
}
