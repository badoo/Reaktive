package com.badoo.reaktive.utils.lock

inline fun <T> Lock.synchronized(block: () -> T): T {
    acquire()
    try {
        return block()
    } finally {
        release()
    }
}

inline fun <T> Lock.use(block: (Lock) -> T): T =
    try {
        block(this)
    } finally {
        destroy()
    }
  
        