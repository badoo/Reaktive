package com.badoo.reaktive.utils.lock

import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.PTHREAD_MUTEX_RECURSIVE
import platform.posix.__darwin_time_t
import platform.posix.gettimeofday
import platform.posix.pthread_cond_broadcast
import platform.posix.pthread_cond_destroy
import platform.posix.pthread_cond_init
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
import platform.posix.timeval

actual class Lock {

    private val arena = Arena()
    private val attr = arena.alloc<pthread_mutexattr_t>()
    private val mutex = arena.alloc<pthread_mutex_t>()

    init {
        pthread_mutexattr_init(attr.ptr)
        pthread_mutexattr_settype(attr.ptr, PTHREAD_MUTEX_RECURSIVE)
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

    private class ConditionImpl(
        private val lockPtr: CPointer<pthread_mutex_t>
    ) : Condition {

        private val arena = Arena()
        private val cond = arena.alloc<pthread_cond_t>()

        init {
            pthread_cond_init(cond.ptr, null)
        }

        override fun await(timeoutNanos: Long) {
            if (timeoutNanos >= 0L) {
                memScoped {
                    // can't use monotonic time, pthread_condattr_setclock() nor clock_gettime(),
                    // iOS does not support it
                    // can't use NSRecursiveLock and NSCondition,
                    // it can't wait less then 1 second and lock can't create condition
                    val tv: timeval = alloc { gettimeofday(ptr, null) }
                    val ts: timespec = alloc()
                    ts.tv_sec = tv.tv_sec
                    ts.tv_nsec = (tv.tv_usec * MICROS_IN_NANOS).convert()
                    ts += timeoutNanos
                    pthread_cond_timedwait(cond.ptr, lockPtr, ts.ptr)
                }
            } else {
                pthread_cond_wait(cond.ptr, lockPtr)
            }
        }

        override fun signal() {
            pthread_cond_broadcast(cond.ptr)
        }

        override fun destroy() {
            pthread_cond_destroy(cond.ptr)
            arena.clear()
        }

        private companion object {
            private const val SECOND_IN_NANOS = 1_000_000_000L
            private const val MICROS_IN_NANOS = 1_000L

            private operator fun timespec.plusAssign(nanos: Long) {
                tv_sec += (nanos / SECOND_IN_NANOS).convert<__darwin_time_t>()
                tv_nsec += (nanos % SECOND_IN_NANOS).convert<__darwin_time_t>()
                if (tv_nsec >= SECOND_IN_NANOS) {
                    tv_sec += 1
                    tv_nsec -= SECOND_IN_NANOS.convert<__darwin_time_t>()
                }
            }
        }
    }
}
