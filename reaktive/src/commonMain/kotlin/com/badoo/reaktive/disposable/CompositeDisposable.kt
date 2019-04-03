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
        var disposablesToDispose: Set<Disposable>? = null
        if (set != null) {
            lock.synchronized {
                disposablesToDispose = set
                set = null
            }
        }
        disposablesToDispose?.forEach(Disposable::dispose)
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

    /**
     * See [add]
     */
    operator fun plusAssign(disposable: Disposable) {
        add(disposable)
    }

    /**
     * Atomically removes the specified [Disposable].
     *
     * @param disposable the [Disposable] to be removed
     * @param dispose if true then the [Disposable] will be disposed if removed, default value is true
     * @return true if the specified [Disposable] was removed (and disposed), false otherwise
     */
    fun remove(disposable: Disposable, dispose: Boolean = true): Boolean {
        var isRemoved = false
        if (set != null) {
            lock.synchronized {
                if (set?.remove(disposable) == true) {
                    isRemoved = true
                }
            }
        }

        if (isRemoved && dispose) {
            disposable.dispose()
        }

        return isRemoved
    }

    /**
     * See [remove]
     */
    operator fun minusAssign(disposable: Disposable) {
        remove(disposable)
    }

    /**
     * Atomically clears all the [Disposable]s
     *
     * @param dispose if true then removed [Disposable]s will be disposed, default value is true
     */
    fun clear(dispose: Boolean = true) {
        if (set != null) {
            lock
                .synchronized {
                    set?.also {
                        set = hashSetOf()
                    }
                }
                ?.takeIf { dispose }
                ?.forEach(Disposable::dispose)
        }
    }
}