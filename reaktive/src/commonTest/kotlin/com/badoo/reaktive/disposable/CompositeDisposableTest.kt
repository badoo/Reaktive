package com.badoo.reaktive.disposable

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompositeDisposableTest {

    private val composite = CompositeDisposable()
    private val disposable1 = Disposable()
    private val disposable2 = Disposable()

    @Test
    fun does_not_dispose_new_disposable_WHEN_not_disposed() {
        composite += disposable1

        assertFalse(disposable1.isDisposed)
    }

    @Test
    fun disposes_all_disposables_WHEN_disposed() {
        composite += disposable1
        composite += disposable2

        composite.dispose()

        assertTrue(disposable1.isDisposed)
        assertTrue(disposable2.isDisposed)
    }

    @Test
    fun disposes_new_disposable_WHEN_already_disposed() {
        composite.dispose()

        composite += disposable1

        assertTrue(disposable1.isDisposed)
    }

    @Test
    fun isDisposed_returns_false_WHEN_not_disposed() {
        assertFalse(composite.isDisposed)
    }

    @Test
    fun isDisposed_returns_true_WHEN_disposed() {
        composite.dispose()

        assertTrue(composite.isDisposed)
    }

    @Test
    fun disposes_all_disposables_WHEN_clear_with_true() {
        composite += disposable1
        composite += disposable2

        composite.clear(dispose = true)

        assertTrue(disposable1.isDisposed)
        assertTrue(disposable2.isDisposed)
    }

    @Test
    fun does_not_dispose_any_disposable_WHEN_clear_with_false() {
        composite += disposable1
        composite += disposable2

        composite.clear(dispose = false)

        assertFalse(disposable1.isDisposed)
        assertFalse(disposable2.isDisposed)
    }

    @Test
    fun does_not_dispose_any_disposable_WHEN_clear_with_false_and_then_dispose() {
        composite += disposable1
        composite += disposable2

        composite.clear(dispose = false)
        composite.dispose()

        assertFalse(disposable1.isDisposed)
        assertFalse(disposable2.isDisposed)
    }
}
