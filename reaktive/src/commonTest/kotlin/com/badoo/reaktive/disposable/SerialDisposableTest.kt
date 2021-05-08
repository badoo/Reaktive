package com.badoo.reaktive.disposable

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SerialDisposableTest {

    private val serialDisposable = SerialDisposable()
    private val disposable = Disposable()

    @Test
    fun does_not_dispose_new_disposable_WHEN_not_disposed() {
        serialDisposable.set(disposable)

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun disposes_new_disposable_WHEN_disposed() {
        serialDisposable.set(disposable)

        serialDisposable.dispose()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun disposes_new_disposable_WHEN_already_disposed() {
        serialDisposable.dispose()

        serialDisposable.set(disposable)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun disposes_previous_disposable_WHEN_new_disposable_is_set() {
        serialDisposable.set(disposable)

        serialDisposable.set(Disposable())

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun does_not_dispose_replacing_disposable_WHEN_not_disposed() {
        serialDisposable.replace(disposable)

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun disposes_replacing_disposable_WHEN_disposed() {
        serialDisposable.replace(disposable)

        serialDisposable.dispose()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun disposes_replacing_disposable_WHEN_already_disposed() {
        serialDisposable.dispose()

        serialDisposable.replace(disposable)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun does_not_dispose_previous_disposable_WHEN_replace_disposable() {
        serialDisposable.set(disposable)

        serialDisposable.replace(Disposable())

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun returns_previous_disposable_WHEN_replace_disposable() {
        serialDisposable.set(disposable)

        val previousDisposable = serialDisposable.replace(Disposable())

        assertSame(disposable, previousDisposable)
    }

    @Test
    fun isDisposed_returns_false_WHEN_not_disposed() {
        assertFalse(serialDisposable.isDisposed)
    }

    @Test
    fun isDisposed_returns_true_WHEN_disposed() {
        serialDisposable.dispose()

        assertTrue(serialDisposable.isDisposed)
    }

    @Test
    fun isDisposed_returns_true_WHEN_already_disposed_and_setDisposable() {
        serialDisposable.dispose()
        serialDisposable.set(disposable)

        assertTrue(serialDisposable.isDisposed)
    }
}
