package com.badoo.reaktive.disposable

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DisposableBuilderWithoutCallbackTest {

    @Test
    fun isDisposed_returns_false_after_creation() {
        val disposable = Disposable()

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun isDisposed_returns_true_IF_disposed() {
        val disposable = Disposable()

        disposable.dispose()

        assertTrue(disposable.isDisposed)
    }
}
