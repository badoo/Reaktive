package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.utils.atomic.AtomicBoolean

class TestCompletableObserver : TestObserver(), CompletableObserver {

    private val _isComplete = AtomicBoolean()
    val isComplete: Boolean get() = _isComplete.value

    override fun onComplete() {
        checkActive()

        _isComplete.value = true
    }

    override fun reset() {
        super.reset()

        _isComplete.value = false
    }

    override fun checkActive() {
        super.checkActive()

        if (isComplete) {
            throw IllegalStateException("Already complete")
        }
    }
}