package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlinx.cinterop.Arena
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.CLOCK_MONOTONIC
import platform.posix.ETIMEDOUT
import platform.posix.PTHREAD_MUTEX_RECURSIVE
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
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

@OptIn(ExperimentalForeignApi::class)
@InternalReaktiveApi
actual open class ConditionLock {

    private val arena = Arena()
    private val mutexAttr = arena.alloc<pthread_mutexattr_t>()
    private val mutex = arena.alloc<pthread_mutex_t>()
    private val condAttr = arena.alloc<pthread_condattr_t>()
    private val cond = arena.alloc<pthread_cond_t>()

    @Suppress("unused") // Must be stored in a property
    @OptIn(ExperimentalNativeApi::class)
    private val cleaner = createCleaner(Resources(arena, mutexAttr, mutex, condAttr, cond), Resources::destroy)

    init {
        pthread_mutexattr_init(mutexAttr.ptr)
        pthread_mutexattr_settype(mutexAttr.ptr, PTHREAD_MUTEX_RECURSIVE.toInt())
        pthread_mutex_init(mutex.ptr, mutexAttr.ptr)
        pthread_condattr_init(condAttr.ptr)
        pthread_condattr_setclock(condAttr.ptr, CLOCK_MONOTONIC)
        pthread_cond_init(cond.ptr, condAttr.ptr)
    }

    actual fun lock() {
        pthread_mutex_lock(mutex.ptr)
    }

    actual fun unlock() {
        pthread_mutex_unlock(mutex.ptr)
    }

    actual fun await(timeout: Duration): Duration =
        if (timeout.isInfinite()) {
            awaitInfinite()
        } else {
            awaitTimed(timeout = timeout.coerceAtLeast(Duration.ZERO))
        }

    private fun awaitInfinite(): Duration {
        val result = pthread_cond_wait(cond.ptr, mutex.ptr)

        if (result == 0) {
            return Duration.INFINITE
        }

        error("Error waiting for condition: $result")
    }

    private fun awaitTimed(timeout: Duration): Duration =
        memScoped {
            val ts = alloc<timespec> { clock_gettime(CLOCK_MONOTONIC, ptr) }
            ts += timeout
            val startNanos = TimeSource.Monotonic.markNow()

            when (val result = pthread_cond_timedwait(cond.ptr, mutex.ptr, ts.ptr)) {
                0 -> startNanos + timeout - TimeSource.Monotonic.markNow()
                ETIMEDOUT -> Duration.ZERO
                else -> error("Error waiting for condition: $result")
            }
        }

    actual fun signal() {
        pthread_cond_broadcast(cond.ptr)
    }

    private companion object {
        private operator fun timespec.plusAssign(duration: Duration) {
            val time = tv_sec.seconds + tv_nsec.nanoseconds + duration
            tv_sec = time.inWholeSeconds
            tv_nsec = time.inWholeNanoseconds % 1.seconds.inWholeNanoseconds
        }
    }

    private class Resources(
        private val arena: Arena,
        private val mutexAttr: pthread_mutexattr_t,
        private val mutex: pthread_mutex_t,
        private val condAttr: pthread_condattr_t,
        private val cond: pthread_cond_t,
    ) {
        fun destroy() {
            pthread_cond_destroy(cond.ptr)
            pthread_condattr_destroy(condAttr.ptr)
            pthread_mutex_destroy(mutex.ptr)
            pthread_mutexattr_destroy(mutexAttr.ptr)
            arena.clear()
        }
    }
}
