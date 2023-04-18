package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
interface Condition {

    fun await(timeoutNanos: Long = -1L)

    fun signal()
}
