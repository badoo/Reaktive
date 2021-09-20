package com.badoo.reaktive.disposable

import kotlin.jvm.Volatile

/**
 * Thread-safe container of one [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
actual open class SerialDisposable actual constructor() : Disposable {

    @Volatile
    private var _isDisposed: Boolean = false
    actual override val isDisposed: Boolean get() = _isDisposed
    private var disposable: Disposable? = null

    /**
     * Disposes this [SerialDisposable] and a stored [Disposable] if any.
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
        replace(disposable)
            ?.dispose()
    }

    /**
     * Atomically either replaces any existing [Disposable]
     * with the specified one or disposes it if wrapper is already disposed.
     * Does not dispose any replaced [Disposable].
     *
     * @param disposable a new [Disposable], will be disposed if wrapper is already dispose
     * @return replaced [Disposable] if any
     */
    actual fun replace(disposable: Disposable?): Disposable? {
        var disposableToDispose: Disposable? = null
        var oldDisposable: Disposable? = null

        synchronized(this) {
            if (_isDisposed) {
                disposableToDispose = disposable
            } else {
                oldDisposable = swapDisposable(disposable)
            }
        }

        disposableToDispose?.dispose()

        return oldDisposable
    }

    private fun swapDisposable(new: Disposable?): Disposable? =
        disposable.also { disposable = new }
}
