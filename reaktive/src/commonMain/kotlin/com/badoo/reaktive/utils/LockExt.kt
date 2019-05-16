package com.badoo.reaktive.utils

internal inline fun <T> Lock.synchronized(block: () -> T): T {
    acquire()
    try {
        return block()
    } finally {
        release()
    }
}