package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.atomic.getAndUpdate

/**
 * Thread-safe collection of [Disposable]
 */
class CompositeDisposable : Disposable {

    private val set = AtomicReference<Set<Disposable>?>(emptySet(), true)
    override val isDisposed: Boolean get() = set.value == null

    override fun dispose() {
        set
            .getAndSet(null)
            ?.forEach(Disposable::dispose)
    }

    /**
     * Atomically either adds the specified [Disposable] or disposes it if container is already disposed.
     * Also removes already disposed Disposables.
     */
    fun add(disposable: Disposable) {
        set
            .getAndUpdate {
                it
                    ?.filterNotTo(hashSetOf(), Disposable::isDisposed)
                    ?.plus(disposable)
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
        set
            .getAndUpdate { it?.let { emptySet() } }
            ?.takeIf { dispose }
            ?.forEach(Disposable::dispose)
    }
}