package com.badoo.reaktive.disposable

/**
 * Thread-safe container of one [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
expect open class DisposableWrapper() : Disposable {

    override val isDisposed: Boolean

    /**
     * Disposes this [DisposableWrapper] and a stored [Disposable] if any.
     * Any future [Disposable] will be immediately disposed.
     */
    override fun dispose()

    /**
     * Atomically either replaces any existing [Disposable]
     * with the specified one or disposes it if wrapper is already disposed.
     * Also disposes any replaced [Disposable].
     */
    fun set(disposable: Disposable?)
}
