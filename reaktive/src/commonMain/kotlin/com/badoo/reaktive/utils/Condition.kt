package com.badoo.reaktive.utils

internal interface Condition {

    fun await(timeoutNanos: Long = -1L)

    fun signal()
}