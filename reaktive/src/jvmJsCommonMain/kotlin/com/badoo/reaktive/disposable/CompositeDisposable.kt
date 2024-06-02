package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.synchronizedCompat
import kotlin.concurrent.Volatile

/**
 * Thread-safe collection of [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
actual open class CompositeDisposable actual constructor() : Disposable {

    private var collection: MutableCollection<Disposable>? = null

    @Volatile
    private var _isDisposed = false
    actual override val isDisposed: Boolean get() = _isDisposed

    /**
     * Atomically disposes the collection and all its [Disposable]s.
     * All future [Disposable]s will be immediately disposed.
     */
    actual override fun dispose() {
        synchronizedCompat(this) {
            _isDisposed = true
            resetDisposables()
        }
            ?.forEach(Disposable::dispose)
    }

    /**
     * Atomically either adds the specified [Disposable] or disposes it if container is already disposed
     *
     * @param disposable the [Disposable] to add
     * @return true if [Disposable] was added to the collection, false otherwise
     */
    actual fun add(disposable: Disposable): Boolean {
        synchronizedCompat(this) {
            if (!_isDisposed) {
                ensureCollection() += disposable

                return true
            }
        }

        disposable.dispose()

        return false
    }

    private fun ensureCollection(): MutableCollection<Disposable> {
        var result = collection

        if (result == null) {
            result = ArrayList()
            collection = result
        } else if (result.size >= SIZE_THRESHOLD_FOR_HASH_SET) {
            result = LinkedHashSet(result)
            collection = result
        }

        return result
    }

    /**
     * Atomically removes the specified [Disposable] from the collection.
     *
     * @param disposable the [Disposable] to remove
     * @param dispose if true then the [Disposable] will be disposed if removed, default value is false
     * @return true if [Disposable] was removed, false otherwise
     */
    actual fun remove(disposable: Disposable, dispose: Boolean): Boolean {
        val result =
            synchronizedCompat(this) {
                collection?.remove(disposable) ?: false
            }

        if (result && dispose) {
            disposable.dispose()
        }

        return result
    }

    /**
     * Atomically clears all the [Disposable]s
     *
     * @param dispose if true then removed [Disposable]s will be disposed, default value is true
     */
    actual fun clear(dispose: Boolean) {
        synchronizedCompat(this, ::resetDisposables)
            ?.takeIf { dispose }
            ?.forEach(Disposable::dispose)
    }

    /**
     * Atomically removes already disposed [Disposable]s
     */
    actual fun purge() {
        synchronizedCompat(this) {
            collection?.removeAll(Disposable::isDisposed)
        }
    }

    private fun resetDisposables(): MutableCollection<Disposable>? = collection.also { collection = null }

    private companion object {
        private const val SIZE_THRESHOLD_FOR_HASH_SET = 32
    }
}
