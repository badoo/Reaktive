package com.badoo.reaktive.utils.lock

interface Condition {

    fun await(timeoutNanos: Long = -1L)

    fun signal()

    fun destroy()
}
