package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.freeze

class TestSingleObserver<T>(autoFreeze: Boolean = true) : TestObserver(), SingleObserver<T> {

    private val _value = AtomicReference<Value<T>?>(null)

    val value: T
        get() =
            requireNotNull(_value.value) { "Single did not success. Assert that with 'assertSuccess()' before accessing the 'value'." }
                .value

    val isSuccess: Boolean get() = _value.value != null

    init {
        if (autoFreeze) {
            freeze()
        }
    }

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

        check(!isSuccess) { "Already succeeded" }
    }

    private class Value<T>(val value: T)
}
