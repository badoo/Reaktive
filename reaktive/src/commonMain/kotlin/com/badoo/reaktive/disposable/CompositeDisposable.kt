package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.atomic.getAndUpdate

/**
 * Thread-safe collection of [Disposable]
 */
class CompositeDisposable : Disposable {

    private val list = AtomicReference<List<Disposable>?>(emptyList(), true)
    override val isDisposed: Boolean get() = list.value == null

    override fun dispose() {
        list
            .getAndSet(null)
            ?.forEach(Disposable::dispose)
    }

    /**
     * Atomically either adds the specified [Disposable] or disposes it if container is already disposed.
     * Also removes already disposed Disposables.
     */
    fun add(disposable: Disposable) {
        list
            .getAndUpdate {
                it
                    ?.toCollection(ArrayList(it.size + 1))
                    ?.apply {
                        removeAll(Disposable::isDisposed)
                        add(disposable)
                    }
            }
            ?: disposable.dispose()
    }

    /**
     * See [add]
     */
    operator fun plusAssign(disposable: Disposable) {
        add(disposable)
    }

    /**
     * Atomically clears all the [Disposable]s
     *
     * @param dispose if true then removed [Disposable]s will be disposed, default value is true
     */
    fun clear(dispose: Boolean = true) {
        list
            .getAndUpdate { it?.let { emptyList() } }
            ?.takeIf { dispose }
            ?.forEach(Disposable::dispose)
    }
}