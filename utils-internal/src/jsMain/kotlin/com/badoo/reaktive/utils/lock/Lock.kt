package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
actual open class Lock {

    actual inline fun <T> synchronized(block: () -> T): T =
        block()
}
