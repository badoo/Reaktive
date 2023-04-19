package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
inline fun <T> Lock.synchronized(block: () -> T): T {
    acquire()
    try {
        return block()
    } finally {
        release()
    }
}
