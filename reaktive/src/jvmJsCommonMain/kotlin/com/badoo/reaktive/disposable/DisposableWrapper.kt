package com.badoo.reaktive.disposable

import kotlin.jvm.Volatile

/**
 * Thread-safe container of one [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
actual open class DisposableWrapper actual constructor() : Disposable {

    @Volatile
    private var _isDisposed: Boolean = false
    actual override val isDisposed: Boolean get() = _isDisposed
    private var disposable: Disposable? = null

    /**
     * Disposes this [DisposableWrapper] and a stored [Disposable] if any.
     * Any future [Disposable] will be immediately disposed.
     */
    actual override fun dispose() {
        synchronized(this) {
            _isDisposed = true
            swapDisposable(null)
        }
            ?.dispose()
    }

    /**
     * Atomically either replaces any existing [Disposable]
     * with the specified one or disposes it if wrapper is already disposed.
     * Also disposes any replaced [Disposable].
     */
    actual fun set(disposable: Disposable?) {
        synchronized(this) {
            if (_isDisposed) disposable else swapDisposable(disposable)
        }
            ?.dispose()
    }

    private fun swapDisposable(new: Disposable?): Disposable? =
        disposable.also { disposable = new }
}
