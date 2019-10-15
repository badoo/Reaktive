package com.badoo.reaktive.disposable

import com.badoo.reaktive.test.doInBackgroundBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class DisposableBuilderWithoutCallbackJvmNativeTest {

    @Test
    fun isDisposed_returns_true_IF_disposed_from_another_thread() {
        val disposable = Disposable()

        doInBackgroundBlocking(block = disposable::dispose)

        assertTrue(disposable.isDisposed)
    }
}
