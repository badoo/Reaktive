package com.badoo.reaktive.disposable

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DisposableBuilderWithCallbackTest {

    @Test
    fun isDisposed_returns_false_after_creation() {
        val disposable = Disposable {}

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun isDisposed_returns_true_IF_disposed() {
        val disposable = Disposable {}

        disposable.dispose()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun calls_callback_WHEN_disposed() {
        var isCalled = false
        val disposable = Disposable { isCalled = true }

        disposable.dispose()

        assertTrue(isCalled)
    }

    @Test
    fun does_not_call_callback_WHEN_disposed_second_time() {
        var isCalled: Boolean
        val disposable = Disposable { isCalled = true }

        disposable.dispose()
        isCalled = false
        disposable.dispose()

        assertFalse(isCalled)
    }
}
