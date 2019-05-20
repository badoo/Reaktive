package com.badoo.reaktive.utils

internal inline fun <T> Lock.synchronized(block: () -> T): T {
    acquire()
    try {
        return block()
    } finally {
        release()
    }
}

internal inline fun <T> Lock.use(block: (Lock) -> T): T =
    try {
        block(this)
    } finally {
        destroy()
    }

internal inline fun <T> useLock(block: (Lock) -> T): T = Lock().use(block)

internal inline fun <T> Lock.useCondition(block: (Condition) -> T): T = newCondition().use(block)