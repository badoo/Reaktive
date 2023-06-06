package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
expect open class Lock() {

    inline fun <T> synchronized(block: () -> T): T
}
