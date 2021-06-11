package com.badoo.reaktive.disposable

/**
 * Thread-safe container of one [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
expect open class SerialDisposable() : Disposable {

    override val isDisposed: Boolean

    /**
     * Disposes this [SerialDisposable] and a stored [Disposable] if any.
     * Any future [Disposable] will be immediately disposed.
     */
    override fun dispose()

    /**
     * Atomically either replaces any existing [Disposable]
     * with the specified one or disposes it if wrapper is already disposed.
     * Also disposes any replaced [Disposable].
     *
     * @param disposable a new [Disposable], will be disposed if wrapper is already dispose
     */
    fun set(disposable: Disposable?)

    /**
     * Atomically either replaces any existing [Disposable]
     * with the specified one or disposes it if wrapper is already disposed.
     * Does not dispose any replaced [Disposable].
     *
     * @param disposable a new [Disposable], will be disposed if wrapper is already dispose
     * @return replaced [Disposable] if any
     */
    fun replace(disposable: Disposable?): Disposable?
}
