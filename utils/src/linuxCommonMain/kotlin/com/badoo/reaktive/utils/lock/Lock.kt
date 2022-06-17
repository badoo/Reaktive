package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.NANOS_IN_SECOND
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.CLOCK_MONOTONIC
import platform.posix.PTHREAD_MUTEX_RECURSIVE
import platform.posix.__syscall_slong_t
import platform.posix.__time_t
import platform.posix.clock_gettime
import platform.posix.pthread_cond_broadcast
import platform.posix.pthread_cond_destroy
import platform.posix.pthread_cond_init
import platform.posix.pthread_cond_t
import platform.posix.pthread_cond_timedwait
import platform.posix.pthread_cond_wait
import platform.posix.pthread_condattr_destroy
import platform.posix.pthread_condattr_init
import platform.posix.pthread_condattr_setclock
import platform.posix.pthread_condattr_t
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

actual class Lock {

    private val arena = Arena()
    private val attr = arena.alloc<pthread_mutexattr_t>()
    private val mutex = arena.alloc<pthread_mutex_t>()

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

    private class ConditionImpl(
        private val lockPtr: CPointer<pthread_mutex_t>
    ) : Condition {

        private val arena = Arena()
        private val attr = arena.alloc<pthread_condattr_t>()
        private val cond = arena.alloc<pthread_cond_t>()

        init {
            pthread_condattr_init(attr.ptr)
            pthread_condattr_setclock(attr.ptr, CLOCK_MONOTONIC)
            pthread_cond_init(cond.ptr, attr.ptr)
        }

        override fun await(timeoutNanos: Long) {
            if (timeoutNanos >= 0L) {
                memScoped {
                    val ts = alloc<timespec> { clock_gettime(CLOCK_MONOTONIC, ptr) }
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
            pthread_condattr_destroy(attr.ptr)
            arena.clear()
        }

        private companion object {
            @OptIn(UnsafeNumber::class)
            private operator fun timespec.plusAssign(nanos: Long) {
                tv_sec += (nanos / NANOS_IN_SECOND).convert<__time_t>()
                tv_nsec += (nanos % NANOS_IN_SECOND).convert<__syscall_slong_t>()
                if (tv_nsec >= NANOS_IN_SECOND) {
                    tv_sec += 1
                    tv_nsec -= NANOS_IN_SECOND.convert<__syscall_slong_t>()
                }
            }
        }
    }
}
