package com.badoo.reaktive.test.maybe

import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference

class TestMaybeObserver<T> : TestObserver(), MaybeObserver<T> {

    private val _value = AtomicReference<Value<T>?>(null)

    val value: T
        get() {
            assertSuccess()
            return _value.value!!.value
        }

    val isSuccess: Boolean get() = _value.value != null
    private val _isComplete = AtomicBoolean()
    val isComplete: Boolean get() = _isComplete.value

    override fun onSuccess(value: T) {
        checkActive()

        _value.value = Value(value)
    }

    override fun onComplete() {
        checkActive()

        _isComplete.value = true
    }

    override fun reset() {
        super.reset()

        _value.value = null
        _isComplete.value = false
    }

    override fun checkActive() {
        super.checkActive()

        if (isSuccess) {
            throw IllegalStateException("Already succeeded")
        }

        if (isComplete) {
            throw IllegalStateException("Already complete")
        }
    }

    private class Value<T>(val value: T)
}