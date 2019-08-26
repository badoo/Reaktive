package com.badoo.reaktive.utils.threadlocal

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.currentThreadId

class ThreadLocalStorage<T : Any>(initialValue: T? = null) : Disposable {

    private val initialThreadId = currentThreadId
    private val key = ThreadLocalState.allocateKey()

    private val _isDisposed = AtomicBoolean()
    override val isDisposed: Boolean
        get() {
            checkCurrentThread()

            return _isDisposed.value
        }

    val value: T?
        get() {
            checkCurrentThread()

            @Suppress("UNCHECKED_CAST")
            return ThreadLocalState[key] as T?
        }

    init {
        if (initialValue != null) {
            set(initialValue)
        }
    }

    fun set(value: T) {
        checkCurrentThread()
        checkDisposed()

        ThreadLocalState[key] = value
    }

    override fun dispose() {
        checkCurrentThread()

        if (_isDisposed.compareAndSet(false, true)) {
            ThreadLocalState[key] = null
        }
    }

    private fun checkCurrentThread() {
        if (currentThreadId != initialThreadId) {
            throw RuntimeException("Accessing ThreadLocalStorage from another threads is prohibited")
        }
    }

    private fun checkDisposed() {
        if (_isDisposed.value) {
            throw IllegalStateException("ThreadLocalStorage is already disposed")
        }
    }
}