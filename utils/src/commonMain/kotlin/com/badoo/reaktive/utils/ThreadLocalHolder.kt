package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.native.concurrent.ThreadLocal

@Deprecated("Use custom solution with kotlin.native.concurrent.WorkerBoundReference")
open class ThreadLocalHolder<T>(initialValue: T? = null) {

    private val originalThreadId = currentThreadId
    private val originalThreadName = currentThreadName
    private val key = ThreadLocalState.allocateKey()

    private val _isDisposed = AtomicBoolean()
    val isDisposed: Boolean
        get() {
            checkCurrentThread()

            return _isDisposed.value
        }

    init {
        if (initialValue != null) {
            set(initialValue)
        }
    }

    fun get(): T? {
        checkCurrentThread()

        @Suppress("UNCHECKED_CAST")
        return ThreadLocalState[key] as T?
    }

    fun set(value: T?) {
        checkCurrentThread()
        checkDisposed()

        ThreadLocalState[key] = value
    }

    fun dispose() {
        checkCurrentThread()

        if (_isDisposed.compareAndSet(false, true)) {
            ThreadLocalState[key] = null
        }
    }

    private fun checkCurrentThread() {
        val threadId = currentThreadId

        check(threadId == originalThreadId) {
            "Accessing ThreadLocalStorage from another threads is prohibited. " +
                "Original thread was ($originalThreadId, $originalThreadName), " +
                "actual thread is ($threadId, $currentThreadName)."
        }
    }

    private fun checkDisposed() {
        check(!_isDisposed.value) { "ThreadLocalStorage is already disposed" }
    }

    @ThreadLocal
    private object ThreadLocalState {
        private val map: MutableMap<Any, Any> = HashMap()
        private var currentKey = 0

        fun allocateKey(): Any = currentKey++

        operator fun get(key: Any): Any? = map[key]

        operator fun set(key: Any, value: Any?) {
            if (value == null) {
                map -= key
            } else {
                map[key] = value
            }
        }
    }
}
