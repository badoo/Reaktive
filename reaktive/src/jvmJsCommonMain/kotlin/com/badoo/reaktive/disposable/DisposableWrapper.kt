package com.badoo.reaktive.disposable

import kotlin.jvm.Volatile

/**
 * Thread-safe container of one [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
actual class DisposableWrapper actual constructor() : Disposable {

    @Volatile
    private var _isDisposed: Boolean = false
    override val isDisposed: Boolean get() = _isDisposed
    private var disposable: Disposable? = null

    /**
     * Disposes this [DisposableWrapper] and a stored [Disposable] if any.
     * Any future [Disposable] will be immediately disposed.
     */
    actual override fun dispose() {
        val disposableToDispose: Disposable?

        synchronized(this) {
            _isDisposed = true
            disposableToDispose = disposable
            disposable = null
        }

        disposableToDispose?.dispose()
    }

    /**
     * Atomically either replaces any existing [Disposable]
     * with the specified one or disposes it if wrapper is already disposed.
     * Also disposes any replaced [Disposable].
     */
    actual fun set(disposable: Disposable?) {
        val disposableToDispose: Disposable?

        synchronized(this) {
            if (_isDisposed) {
                disposableToDispose = disposable
            } else {
                disposableToDispose = this.disposable
                this.disposable = disposable
            }
        }

        disposableToDispose?.dispose()
    }
}
