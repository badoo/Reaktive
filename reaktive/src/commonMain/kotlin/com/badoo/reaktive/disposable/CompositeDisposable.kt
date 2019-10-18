package com.badoo.reaktive.disposable

/**
 * Thread-safe collection of [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
expect class CompositeDisposable() : Disposable {

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
