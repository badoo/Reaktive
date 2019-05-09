package com.badoo.reaktive.utils

internal interface Condition {

    fun await(timeoutNanos: Long = 0L)

    fun signal()

    fun destroy()
}