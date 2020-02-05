package com.badoo.reaktive.disposable

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DisposableWrapperTest {

    private val wrapper = DisposableWrapper()
    private val disposable = Disposable()

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

        wrapper.set(Disposable())

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun does_not_dispose_replacing_disposable_WHEN_not_disposed() {
        wrapper.replace(disposable)

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun disposes_replacing_disposable_WHEN_disposed() {
        wrapper.replace(disposable)

        wrapper.dispose()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun disposes_replacing_disposable_WHEN_already_disposed() {
        wrapper.dispose()

        wrapper.replace(disposable)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun does_not_dispose_previous_disposable_WHEN_replace_disposable() {
        wrapper.set(disposable)

        wrapper.replace(Disposable())

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun returns_previous_disposable_WHEN_replace_disposable() {
        wrapper.set(disposable)

        val previousDisposable = wrapper.replace(Disposable())

        assertSame(disposable, previousDisposable)
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

    @Test
    fun isDisposed_returns_true_WHEN_already_disposed_and_setDisposable() {
        wrapper.dispose()
        wrapper.set(disposable)

        assertTrue(wrapper.isDisposed)
    }
}
