package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndChange

/**
 * Thread-safe container of one [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
actual open class SerialDisposable actual constructor() : Disposable {

    private val ref = AtomicReference<Disposable?>(null)
    actual override val isDisposed: Boolean get() = ref.value === disposed

    /**
     * Disposes this [SerialDisposable] and a stored [Disposable] if any.
     * Any future [Disposable] will be immediately disposed.
     */
    actual override fun dispose() {
        clearAndDispose()?.dispose()
    }

    internal actual fun clearAndDispose(): Disposable? =
        ref.getAndSet(disposed)

    /**
     * Atomically either replaces any existing [Disposable]
     * with the specified one or disposes it if wrapper is already disposed.
     * Also disposes any replaced [Disposable].
     *
     * @param disposable a new [Disposable], will be disposed if wrapper is already dispose
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
        val oldDisposable = ref.getAndChange { if (it === disposed) it else disposable }

        if (oldDisposable !== disposed) {
            return oldDisposable
        }

        disposable?.dispose()

        return null
    }

    private companion object {
        private val disposed = Disposable()
    }
}
