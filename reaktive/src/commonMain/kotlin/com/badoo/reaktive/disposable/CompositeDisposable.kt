package com.badoo.reaktive.disposable

/**
 * Thread-safe collection of [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
expect open class CompositeDisposable() : Disposable {

    /**
     * Disposes the [CompositeDisposable] and all its [Disposable]s.
     * All future [Disposable]s will be immediately disposed.
     */
    override fun dispose()

    /**
     * Atomically either adds the specified [Disposable] or disposes it if container is already disposed.
     * Also removes already disposed Disposables.
     */
    fun add(disposable: Disposable)

    /**
     * See [add]
     */
    operator fun plusAssign(disposable: Disposable)

    /**
     * Atomically clears all the [Disposable]s
     *
     * @param dispose if true then removed [Disposable]s will be disposed, default value is true
     */
    fun clear(dispose: Boolean = true)
}
