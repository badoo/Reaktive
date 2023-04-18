package com.badoo.reaktive.utils.clock

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
interface Clock {

    val uptimeMillis: Long

    val uptimeNanos: Long
}
