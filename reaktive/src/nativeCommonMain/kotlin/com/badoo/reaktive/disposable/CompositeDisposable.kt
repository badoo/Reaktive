package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.atomic.getAndUpdate
import com.badoo.reaktive.utils.atomic.update

/**
 * Thread-safe collection of [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
actual open class CompositeDisposable actual constructor() : Disposable {

    private val list = AtomicReference<Set<Disposable>?>(emptySet())
    override val isDisposed: Boolean get() = list.value == null

    /**
     * Atomically disposes the collection and all its [Disposable]s.
     * All future [Disposable]s will be immediately disposed.
     */
    actual override fun dispose() {
        list
            .getAndSet(null)
            ?.forEach(Disposable::dispose)
    }

    /**
     * Atomically either adds the specified [Disposable] or disposes it if container is already disposed
     *
     * @param disposable the [Disposable] to add
     * @return true if [Disposable] was added to the collection, false otherwise
     */
    actual fun add(disposable: Disposable): Boolean {
        val isUpdated = updateSet { it + disposable }

        if (!isUpdated) {
            disposable.dispose()
        }

        return isUpdated
    }

    /**
     * Atomically removes the specified [Disposable] from the collection.
     *
     * @param disposable the [Disposable] to remove
     * @param dispose if true then the [Disposable] will be disposed if removed, default value is false
     * @return true if [Disposable] was removed, false otherwise
     */
    actual fun remove(disposable: Disposable, dispose: Boolean): Boolean {
        val isUpdated =
            updateSet { oldList ->
                oldList
                    .minus(disposable)
                    .takeIf { it.size < oldList.size }
            }

        if (isUpdated && dispose) {
            disposable.dispose()
        }

        return isUpdated
    }

    private inline fun updateSet(block: (Set<Disposable>) -> Set<Disposable>?): Boolean {
        var isUpdated = false

        list.update { oldSet: Set<Disposable>? ->
            val newSet: Set<Disposable>? = oldSet?.let(block)
            isUpdated = newSet != null
            newSet ?: oldSet
        }

        return isUpdated
    }

    /**
     * Atomically clears all the [Disposable]s
     *
     * @param dispose if true then removed [Disposable]s will be disposed, default value is true
     */
    actual fun clear(dispose: Boolean) {
        list
            .getAndUpdate { it?.let { emptySet() } }
            ?.takeIf { dispose }
            ?.forEach(Disposable::dispose)
    }

    /**
     * Atomically removes already disposed [Disposable]s
     */
    actual fun purge() {
        list.update {
            it?.filterNotTo(LinkedHashSet(it.size), Disposable::isDisposed)
        }
    }
}
