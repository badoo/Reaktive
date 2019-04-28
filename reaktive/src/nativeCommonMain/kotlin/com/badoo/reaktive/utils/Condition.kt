package com.badoo.reaktive.utils

import kotlinx.cinterop.Arena
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import platform.posix.pthread_cond_destroy
import platform.posix.pthread_cond_init
import platform.posix.pthread_cond_signal
import platform.posix.pthread_cond_t
import platform.posix.pthread_cond_timedwait
import platform.posix.pthread_cond_wait
import platform.posix.timespec
import kotlin.system.getTimeNanos

internal class Condition(
    private val lock: Lock
) {

    private val arena = Arena()
    private val cond: pthread_cond_t = arena.alloc()

    init {
        pthread_cond_init(cond.ptr, null)
    }

    fun await(timeoutNanos: Long) {
        val a = Arena()
        try {
            val t: timespec = a.alloc()
            t.tv_sec = 0
            t.tv_nsec = getTimeNanos() + timeoutNanos
            pthread_cond_timedwait(cond.ptr, lock.ptr, t.ptr)
        } finally {
            a.clear()
        }
    }

    fun await() {
        pthread_cond_wait(cond.ptr, lock.ptr)
    }

    fun signal() {
        pthread_cond_signal(cond.ptr)
    }

    fun destroy() {
        pthread_cond_destroy(cond.ptr)
        arena.clear()
    }
}