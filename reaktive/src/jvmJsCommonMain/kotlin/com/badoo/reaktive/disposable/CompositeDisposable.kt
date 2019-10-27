package com.badoo.reaktive.disposable

import kotlin.jvm.Volatile

/**
 * Thread-safe collection of [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
actual open class CompositeDisposable actual constructor() : Disposable {

    private var list: MutableList<Disposable>? = null
    @Volatile
    private var _isDisposed = false
    override val isDisposed: Boolean get() = _isDisposed

    /**
     * Disposes the [CompositeDisposable] and all its [Disposable]s.
     * All future [Disposable]s will be immediately disposed.
     */
    actual override fun dispose() {
        synchronized(this) {
            _isDisposed = true
            resetDisposables()
        }
            ?.forEach(Disposable::dispose)
    }

    /**
     * Atomically either adds the specified [Disposable] or disposes it if container is already disposed.
     * Also removes already disposed Disposables.
     */
    actual fun add(disposable: Disposable) {
        synchronized(this) {
            if (!_isDisposed) {
                val listToAdd = list ?: ArrayList<Disposable>().also { list = it }
                listToAdd += disposable

                return
            }
        }

        disposable.dispose()
    }

    /**
     * See [add]
     */
    actual operator fun plusAssign(disposable: Disposable) {
        add(disposable)
    }

    /**
     * Atomically clears all the [Disposable]s
     *
     * @param dispose if true then removed [Disposable]s will be disposed, default value is true
     */
    actual fun clear(dispose: Boolean) {
        synchronized(this, ::resetDisposables)
            ?.takeIf { dispose }
            ?.forEach(Disposable::dispose)
    }

    private fun resetDisposables(): MutableList<Disposable>? = list.also { list = null }
}
