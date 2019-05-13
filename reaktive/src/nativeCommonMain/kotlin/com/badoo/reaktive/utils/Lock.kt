package com.badoo.reaktive.utils

import kotlinx.cinterop.CValue
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.useContents
import platform.posix.PTHREAD_MUTEX_RECURSIVE
import platform.posix.pthread_cond_init
import platform.posix.pthread_cond_signal
import platform.posix.pthread_cond_t
import platform.posix.pthread_cond_timedwait
import platform.posix.pthread_cond_wait
import platform.posix.pthread_mutex_init
import platform.posix.pthread_mutex_lock
import platform.posix.pthread_mutex_t
import platform.posix.pthread_mutex_unlock
import platform.posix.pthread_mutexattr_init
import platform.posix.pthread_mutexattr_settype
import platform.posix.pthread_mutexattr_t
import platform.posix.timespec
import kotlin.system.getTimeNanos

internal actual class Lock {

    private val attr = cValue<pthread_mutexattr_t>()
    private val mutex = cValue<pthread_mutex_t>()

    init {
        pthread_mutexattr_init(attr)
        pthread_mutexattr_settype(attr, PTHREAD_MUTEX_RECURSIVE.toInt())
        pthread_mutex_init(mutex, attr)
    }

    actual fun acquire() {
        pthread_mutex_lock(mutex)
    }

    actual fun release() {
        pthread_mutex_unlock(mutex)
    }

    actual fun newCondition(): Condition = ConditionImpl(mutex)

    internal class ConditionImpl(
        private val lockPtr: CValue<pthread_mutex_t>
    ) : Condition {

        private val cond = cValue<pthread_cond_t>()

        init {
            pthread_cond_init(cond, null)
        }

        override fun await(timeoutNanos: Long) {
            if (timeoutNanos >= 0L) {
                val t = cValue<timespec>()
                (getTimeNanos() + timeoutNanos).toTimespec(t)
                pthread_cond_timedwait(cond, lockPtr, t)
            } else {
                pthread_cond_wait(cond, lockPtr)
            }
        }

        override fun signal() {
            pthread_cond_signal(cond)
        }

        private companion object {
            private const val SECOND_IN_NANOS = 1000000000L

            private fun Long.toTimespec(time: CValue<timespec>) {
                val secs = this / SECOND_IN_NANOS
                time.useContents {
                    tv_sec = secs.convert()
                    tv_nsec = (this@toTimespec - (secs * SECOND_IN_NANOS)).convert()
                }
            }
        }
    }
}