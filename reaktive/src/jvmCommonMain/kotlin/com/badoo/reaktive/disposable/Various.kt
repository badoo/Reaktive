@file:JvmName("VariousJvm")
package com.badoo.reaktive.disposable

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.jvm.JvmName

@Suppress("FunctionName")
actual inline fun Disposable(crossinline onDispose: () -> Unit): Disposable =
    object : AtomicBoolean(), Disposable {
        override val isDisposed: Boolean get() = get()

        override fun dispose() {
            if (compareAndSet(false, true)) {
                onDispose()
            }
        }
    }

@Suppress("FunctionName")
actual fun Disposable(): Disposable = SimpleDisposable()

private class SimpleDisposable : Disposable {
    @Volatile
    override var isDisposed: Boolean = false
        private set

    override fun dispose() {
        isDisposed = true
    }
}
