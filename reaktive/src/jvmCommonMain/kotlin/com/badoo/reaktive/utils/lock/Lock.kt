package com.badoo.reaktive.utils.lock

actual class Lock {

    private val delegate = java.util.concurrent.locks.ReentrantLock()

    actual fun acquire() {
        delegate.lock()
    }

    actual fun release() {
        delegate.unlock()
    }

    actual fun destroy() {
        // no-op
    }

    actual fun newCondition(): Condition = ConditionImpl(this@Lock.delegate.newCondition())

    private class ConditionImpl(
        private val delegate: java.util.concurrent.locks.Condition
    ) : Condition {
        override fun await(timeoutNanos: Long) {
            var isInterrupted = Thread.interrupted() // "Thread.interrupted()" clears "interrupted" status of the current thread
            try {
                if (timeoutNanos >= 0L) {
                    delegate.awaitNanos(timeoutNanos)
                } else {
                    delegate.await()
                }
            } catch (e: InterruptedException) {
                isInterrupted = true
            } finally {
                if (isInterrupted) {
                    Thread.currentThread().interrupt() // Set "interrupted" status of the current thread
                }
            }
        }

        override fun signal() {
            delegate.signalAll()
        }

        override fun destroy() {
            // no-op
        }
    }
}