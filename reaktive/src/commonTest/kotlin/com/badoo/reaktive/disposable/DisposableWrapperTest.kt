package com.badoo.reaktive.disposable

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DisposableWrapperTest {

    private val wrapper = DisposableWrapper()
    private val disposable = disposable()

    @Test
    fun does_not_dispose_new_disposable_WHEN_not_disposed() {
        wrapper.set(disposable)

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun disposes_new_disposable_WHEN_disposed() {
        wrapper.set(disposable)

        wrapper.dispose()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun disposes_new_disposable_WHEN_already_disposed() {
        wrapper.dispose()

        wrapper.set(disposable)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun disposes_previous_disposable_WHEN_new_disposable_is_set() {
        wrapper.set(disposable)

        wrapper.set(disposable())

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun isDisposed_returns_false_WHEN_not_disposed() {
        assertFalse(wrapper.isDisposed)
    }

    @Test
    fun isDisposed_returns_true_WHEN_disposed() {
        wrapper.dispose()

        assertTrue(wrapper.isDisposed)
    }
}
