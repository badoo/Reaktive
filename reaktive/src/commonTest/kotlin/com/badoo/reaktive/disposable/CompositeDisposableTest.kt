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
    fun add_returns_true_WHEN_not_disposed() {
        val result = composite.add(disposable1)

        assertTrue(result)
    }

    @Test
    fun add_returns_false_WHEN_already_disposed() {
        composite.dispose()

        val result = composite.add(disposable1)

        assertFalse(result)
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

    @Test
    fun disposes_disposable_WHEN_contains_and_remove_with_true() {
        composite += disposable1
        composite += disposable2

        composite.remove(disposable1, dispose = true)

        assertTrue(disposable1.isDisposed)
    }

    @Test
    fun does_not_dispose_other_disposables_WHEN_remove_with_true() {
        composite += disposable1
        composite += disposable2

        composite.remove(disposable1, dispose = true)

        assertFalse(disposable2.isDisposed)
    }

    @Test
    fun does_not_dispose_disposable_WHEN_not_contains_and_remove_with_true() {
        composite.remove(disposable1, dispose = true)

        assertFalse(disposable1.isDisposed)
    }

    @Test
    fun remove_returns_true_WHEN_contains_disposable_and_with_false() {
        composite += disposable1

        val result = composite.remove(disposable1, dispose = false)

        assertTrue(result)
    }

    @Test
    fun remove_returns_true_WHEN_contains_disposable_and_with_true() {
        composite += disposable1

        val result = composite.remove(disposable1, dispose = true)

        assertTrue(result)
    }

    @Test
    fun remove_returns_false_WHEN_not_contains_disposable_and_with_true() {
        composite += disposable2

        val result = composite.remove(disposable1, dispose = true)

        assertFalse(result)
    }

    @Test
    fun remove_returns_false_WHEN_not_contains_disposable_and_with_false() {
        composite += disposable2

        val result = composite.remove(disposable1, dispose = false)

        assertFalse(result)
    }

    @Test
    fun remove_returns_false_WHEN_disposable_was_added_and_with_false_and_already_disposed() {
        composite += disposable1
        composite.dispose()

        val result = composite.remove(disposable1, dispose = false)

        assertFalse(result)
    }

    @Test
    fun remove_returns_false_WHEN_disposable_was_added_and_with_true_and_already_disposed() {
        composite += disposable1
        composite.dispose()

        val result = composite.remove(disposable1, dispose = true)

        assertFalse(result)
    }

    @Test
    fun disposes_disposable_WHEN_remove_non_existing_disposable_and_then_remove_existing_disposable_with_true() {
        composite += disposable1

        composite.remove(disposable2)
        composite.remove(disposable1, dispose = true)

        assertTrue(disposable1.isDisposed)
    }

    @Test
    fun checks_all_disposables_WHEN_purge() {
        var isChecked1 = false

        val disposable1 =
            emptyDisposable {
                isChecked1 = true
                false
            }

        var isChecked2 = false

        val disposable2 =
            emptyDisposable {
                isChecked2 = true
                false
            }

        composite += disposable1
        composite += disposable2

        composite.purge()

        assertTrue(isChecked1)
        assertTrue(isChecked2)
    }

    @Test
    fun does_not_check_disposed_disposables_WHEN_purge_second_time() {
        var isChecked1: Boolean

        val disposable1 =
            emptyDisposable {
                isChecked1 = true
                true
            }

        val disposable2 = emptyDisposable { false }

        var isChecked3: Boolean

        val disposable3 =
            emptyDisposable {
                isChecked3 = true
                true
            }

        composite += disposable1
        composite += disposable2
        composite += disposable3
        composite.purge()
        isChecked1 = false
        isChecked3 = false

        composite.purge()

        assertFalse(isChecked1)
        assertFalse(isChecked3)
    }

    @Test
    fun checks_not_disposed_disposables_WHEN_purge_second_time() {
        var isChecked1: Boolean

        val disposable1 =
            emptyDisposable {
                isChecked1 = true
                false
            }

        val disposable2 = emptyDisposable { true }

        var isChecked3: Boolean

        val disposable3 =
            emptyDisposable {
                isChecked3 = true
                false
            }

        composite += disposable1
        composite += disposable2
        composite += disposable3
        composite.purge()
        isChecked1 = false
        isChecked3 = false

        composite.purge()

        assertTrue(isChecked1)
        assertTrue(isChecked3)
    }

    private fun emptyDisposable(isDisposed: () -> Boolean): Disposable =
        object : Disposable {
            override val isDisposed: Boolean get() = isDisposed.invoke()

            override fun dispose() {
                // no-op
            }
        }
}
