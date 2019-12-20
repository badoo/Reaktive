package com.badoo.reaktive.disposable

/**
 * Thread-safe [Disposable] collection
 */
@Suppress("EmptyDefaultConstructor")
expect open class CompositeDisposable() : Disposable {

    /**
     * Atomically disposes the collection and all its [Disposable]s.
     * All future [Disposable]s will be immediately disposed.
     */
    override fun dispose()

    /**
     * Atomically either adds the specified [Disposable] or disposes it if container is already disposed
     *
     * @param disposable the [Disposable] to add
     * @return true if [Disposable] was added to the collection, false otherwise
     */
    fun add(disposable: Disposable): Boolean

    /**
     * Atomically removes the specified [Disposable] from the collection.
     *
     * @param disposable the [Disposable] to remove
     * @param dispose if true then the [Disposable] will be disposed if removed, default value is false
     * @return true if [Disposable] was removed, false otherwise
     */
    fun remove(disposable: Disposable, dispose: Boolean = false): Boolean

    /**
     * Atomically clears all the [Disposable]s
     *
     * @param dispose if true then removed [Disposable]s will be disposed, default value is true
     */
    fun clear(dispose: Boolean = true)

    /**
     * Atomically removes already disposed [Disposable]s
     */
    fun purge()
}
