package com.badoo.reaktive.utils.clock

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.system.getTimeMillis
import kotlin.system.getTimeNanos

@InternalReaktiveApi
actual object DefaultClock : Clock {

    override val uptimeMillis: Long get() = getTimeMillis()

    override val uptimeNanos: Long get() = getTimeNanos()
}
