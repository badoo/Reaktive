package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

/**
 * Thread-safe container of one [Disposable]
 */
class DisposableWrapper(
    private var disposable: Disposable? = null
) : Disposable {

    private val lock = newLock()
    private var _isDisposed = false
    override val isDisposed: Boolean get() = lock.synchronized { _isDisposed }

    override fun dispose() {
        if (!_isDisposed) {
            var d: Disposable? = null
            lock.synchronized {
                if (_isDisposed) {
                    return
                }
                _isDisposed = true
                d = disposable
                disposable = null
            }
            d?.dispose()
        }
    }

    /**
     * Atomically either sets the specified [Disposable] or disposes it if wrapper is already disposed
     */
    fun set(disposable: Disposable?): Disposable? {
        if (!_isDisposed) {
            lock.synchronized {
                if (!_isDisposed) {
                    val old = this.disposable
                    this.disposable = disposable
                    return old
                }
            }
        }

        disposable?.dispose()

        return null
    }
}