package com.badoo.reaktive.utils

import kotlin.system.getTimeNanos
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import platform.posix.PTHREAD_MUTEX_RECURSIVE
import platform.posix.pthread_cond_destroy
import platform.posix.pthread_cond_init
import platform.posix.pthread_cond_signal
import platform.posix.pthread_cond_t
import platform.posix.pthread_cond_timedwait
import platform.posix.pthread_cond_wait
import platform.posix.pthread_mutex_destroy
import platform.posix.pthread_mutex_init
import platform.posix.pthread_mutex_lock
import platform.posix.pthread_mutex_t
import platform.posix.pthread_mutex_unlock
import platform.posix.pthread_mutexattr_destroy
import platform.posix.pthread_mutexattr_init
import platform.posix.pthread_mutexattr_settype
import platform.posix.pthread_mutexattr_t
import platform.posix.timespec

internal actual class Lock {

    private val arena = Arena()
    private val attr: pthread_mutexattr_t = arena.alloc()
    private val mutex: pthread_mutex_t = arena.alloc()

    init {
        pthread_mutexattr_init(attr.ptr)
        pthread_mutexattr_settype(attr.ptr, PTHREAD_MUTEX_RECURSIVE.toInt())
        pthread_mutex_init(mutex.ptr, attr.ptr)
    }

    actual fun acquire() {
        pthread_mutex_lock(mutex.ptr)
    }

    actual fun release() {
        pthread_mutex_unlock(mutex.ptr)
    }

    actual fun destroy() {
        pthread_mutex_destroy(mutex.ptr)
        pthread_mutexattr_destroy(attr.ptr)
        arena.clear()
    }

    actual fun newCondition(): Condition = ConditionImpl(mutex.ptr)

    internal class ConditionImpl(
        private val lockPtr: CPointer<pthread_mutex_t>
    ) : Condition {

        private val arena = Arena()
        private val cond: pthread_cond_t = arena.alloc()

        init {
            pthread_cond_init(cond.ptr, null)
        }

        override fun await(timeoutNanos: Long) {
            if (timeoutNanos >= 0L) {
                val a = Arena()
                try {
                    val t: timespec = a.alloc()
                    (getTimeNanos() + timeoutNanos).toTimespec(t)
                    pthread_cond_timedwait(cond.ptr, lockPtr, t.ptr)
                } finally {
                    a.clear()
                }
            } else {
                pthread_cond_wait(cond.ptr, lockPtr)
            }
        }

        override fun signal() {
            pthread_cond_signal(cond.ptr)
        }

        override fun destroy() {
            pthread_cond_destroy(cond.ptr)
            arena.clear()
        }

        private companion object {
            private const val SECOND_IN_NANOS = 1000000000L

            private fun Long.toTimespec(time: timespec) {
                val secs = this / SECOND_IN_NANOS
                time.tv_sec = secs.convert()
                time.tv_nsec = (this - (secs * SECOND_IN_NANOS)).convert()
            }
        }
    }
}