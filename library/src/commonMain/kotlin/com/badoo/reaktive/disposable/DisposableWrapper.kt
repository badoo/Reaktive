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
            var disposableToDispose: Disposable? = null
            lock.synchronized {
                if (!_isDisposed) {
                    _isDisposed = true
                    disposableToDispose = disposable
                    disposable = null
                }
            }
            disposableToDispose?.dispose()
        }
    }

    /**
     * Atomically either replaces any existing [Disposable] with the specified one or disposes it if wrapper is already disposed.
     * Also disposes any replaced [Disposable].
     */
    fun set(disposable: Disposable?) {
        var disposableToDispose: Disposable? = disposable
        if (!_isDisposed) {
            lock.synchronized {
                if (!_isDisposed) {
                    disposableToDispose = this.disposable
                    this.disposable = disposable
                }
            }
        }

        disposableToDispose?.dispose()
    }
}