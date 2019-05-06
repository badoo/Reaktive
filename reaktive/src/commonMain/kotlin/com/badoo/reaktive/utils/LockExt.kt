package com.badoo.reaktive.utils

internal inline fun Lock.synchronized(block: () -> Unit) {
    acquire()
    try {
        block()
    } finally {
        release()
    }
}