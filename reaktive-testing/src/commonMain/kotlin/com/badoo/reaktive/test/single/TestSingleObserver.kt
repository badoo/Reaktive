package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.utils.atomic.AtomicReference

class TestSingleObserver<T> : TestObserver(), SingleObserver<T> {

    private val _value = AtomicReference<Value<T>?>(null)

    val value: T
        get() {
            assertSuccess()
            return _value.value!!.value
        }

    val isSuccess: Boolean get() = _value.value != null

    override fun onSuccess(value: T) {
        checkActive()

        _value.value = Value(value)
    }

    override fun reset() {
        super.reset()

        _value.value = null
    }

    override fun checkActive() {
        super.checkActive()

        if (isSuccess) {
            throw IllegalStateException("Already succeeded")
        }
    }

    private class Value<T>(val value: T)
}
