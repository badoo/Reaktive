package com.badoo.reaktive.disposable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SerialDisposableTest {
    private val serialDisposable = SerialDisposable()
    private val disposable1 = Disposable()
    private val disposable2 = Disposable()

    @Test
    fun does_not_dispose_new_disposable_WHEN_not_disposed() {
        serialDisposable.disposable = disposable1

        assertFalse(disposable1.isDisposed)
    }

    @Test
    fun disposes_current_disposable_WHEN_disposed() {
        serialDisposable.disposable = disposable1
        serialDisposable.dispose()

        assertTrue(disposable1.isDisposed)
    }

    @Test
    fun disposes_new_disposable_WHEN_already_disposed() {
        serialDisposable.dispose()
        serialDisposable.disposable = disposable1

        assertTrue(disposable1.isDisposed)
    }

    @Test
    fun disposes_current_disposable_WHEN_new_assigned() {
        serialDisposable.disposable = disposable1
        serialDisposable.disposable = disposable2

        assertTrue(disposable1.isDisposed)
    }

    @Test
    fun disposable_returns_current_disposable_WHEN_not_disposed() {
        serialDisposable.disposable = disposable1

        assertEquals(disposable1, serialDisposable.disposable)
    }

    @Test
    fun disposable_no_longer_returns_current_disposable_WHEN_disposed() {
        serialDisposable.disposable = disposable1
        serialDisposable.dispose()

        assertNotEquals(disposable1, serialDisposable.disposable)
    }

    @Test
    fun disposable_does_not_return_newly_set_disposable_WHEN_disposed() {
        serialDisposable.dispose()
        serialDisposable.disposable = disposable1

        assertNotEquals(disposable1, serialDisposable.disposable)
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
}
