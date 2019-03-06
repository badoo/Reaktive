package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

/**
 * Thread-safe collection of [Disposable]
 */
class CompositeDisposable : Disposable {

    private val lock = newLock()
    private var set: MutableSet<Disposable>? = hashSetOf()
    override val isDisposed: Boolean get() = lock.synchronized { set == null }

    override fun dispose() {
        val disposables = set
        if (disposables != null) {
            lock.synchronized {
                if (set == null) {
                    return
                }
                set = null
            }
            disposables.forEach(Disposable::dispose)
        }
    }

    /**
     * Atomically either adds the specified [Disposable] or disposes it if container is already disposed.
     * Also removes already disposed Disposables.
     */
    fun add(disposable: Disposable) {
        if (set != null) {
            lock.synchronized {
                set?.also {
                    it.removeAll(Disposable::isDisposed)
                    it.add(disposable)
                    return
                }
            }
        }

        disposable.dispose()
    }

    operator fun plusAssign(disposable: Disposable) {
        add(disposable)
    }

    /**
     * Atomically removes but does not disposes the specified [Disposable]
     */
    fun remove(disposable: Disposable) {
        if (set != null) {
            lock.synchronized {
                set?.remove(disposable)
            }
        }
    }

    operator fun minusAssign(disposable: Disposable) {
        remove(disposable)
    }

    /**
     * Atomically clears all the Disposables
     *
     * @param dispose if true then removed Disposables will be disposed
     */
    fun clear(dispose: Boolean = false) {
        if (set != null) {
            lock
                .synchronized {
                    set?.also { set = hashSetOf() }
                }
                ?.takeIf { dispose }
                ?.forEach(Disposable::dispose)
        }
    }
}