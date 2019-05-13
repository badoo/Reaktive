package com.badoo.reaktive.utils

import java.util.concurrent.TimeUnit

internal actual class Lock {

    private val delegate = java.util.concurrent.locks.ReentrantLock()

    actual fun acquire() {
        delegate.lock()
    }

    actual fun release() {
        delegate.unlock()
    }

    actual fun newCondition(): Condition = ConditionImpl(this@Lock.delegate.newCondition())

    private class ConditionImpl(
        private val delegate: java.util.concurrent.locks.Condition
    ) : Condition {
        override fun await(timeoutNanos: Long) {
            if (timeoutNanos >= 0L) {
                delegate.awaitNanos(timeoutNanos)
            } else {
                delegate.await()
            }
        }

        override fun signal() {
            delegate.signalAll()
        }
    }
}