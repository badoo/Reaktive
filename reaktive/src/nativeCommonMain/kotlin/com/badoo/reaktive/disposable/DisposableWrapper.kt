package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate

/**
 * Thread-safe container of one [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
actual open class DisposableWrapper actual constructor() : Disposable {

    private val ref = AtomicReference<Holder?>(Holder(null))
    actual override val isDisposed: Boolean get() = ref.value == null

    /**
     * Disposes this [DisposableWrapper] and a stored [Disposable] if any.
     * Any future [Disposable] will be immediately disposed.
     */
    actual override fun dispose() {
        setHolder(null)
    }

    /**
     * Atomically either replaces any existing [Disposable]
     * with the specified one or disposes it if wrapper is already disposed.
     * Also disposes any replaced [Disposable].
     */
    actual fun set(disposable: Disposable?) {
        setHolder(Holder(disposable))
    }

    private fun setHolder(holder: Holder?) {
        ref
            .getAndUpdate { oldHolder ->
                if (oldHolder == null) {
                    holder?.dispose()
                }

                holder
            }
            ?.dispose()
    }

    private class Holder(
        private val disposable: Disposable?
    ) {
        fun dispose() {
            disposable?.dispose()
        }
    }
}
