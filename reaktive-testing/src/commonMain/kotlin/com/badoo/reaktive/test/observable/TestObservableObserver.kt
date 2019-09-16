package com.badoo.reaktive.test.observable

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.clear
import com.badoo.reaktive.utils.atomic.plusAssign

class TestObservableObserver<T> : TestObserver(), ObservableObserver<T> {

    private val _values = atomicList<T>()
    val values: List<T> get() = _values.value
    private val _isComplete = AtomicBoolean()
    val isComplete: Boolean get() = _isComplete.value

    override fun onNext(value: T) {
        checkActive()

        _values += value
    }

    override fun onComplete() {
        checkActive()

        _isComplete.value = true
    }

    override fun reset() {
        super.reset()

        _values.clear()
        _isComplete.value = false
    }

    override fun checkActive() {
        super.checkActive()

        check(!isComplete) { "Already complete" }
    }
}
