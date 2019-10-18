package com.badoo.reaktive.utils.clock

interface Clock {

    val uptimeMillis: Long

    val uptimeNanos: Long
}
