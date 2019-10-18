package com.badoo.reaktive.disposable

import kotlin.jvm.Volatile

/**
 * Thread-safe collection of [Disposable]
 */
@Suppress("EmptyDefaultConstructor")
actual class CompositeDisposable actual constructor() : Disposable {

    private var list: MutableList<Disposable>? = null
    @Volatile
    private var _isDisposed = false
    override val isDisposed: Boolean get() = _isDisposed

    /**
     * {@inheritDoc}
     * Disposes the [CompositeDisposable] and all its [Disposable]s.
     * All future [Disposable]s will be immediately disposed.
     */
    override fun dispose() {
        val listToDispose: List<Disposable>?

        synchronized(this) {
            _isDisposed = true
            listToDispose = list
            list = null
        }

        listToDispose?.forEach(Disposable::dispose)
    }

    /**
     * Atomically either adds the specified [Disposable] or disposes it if container is already disposed.
     * Also removes already disposed Disposables.
     */
    actual fun add(disposable: Disposable) {
        synchronized(this) {
            if (!_isDisposed) {
                var listToAdd = list
                if (listToAdd == null) {
                    listToAdd = ArrayList()
                    list = listToAdd
                }

                listToAdd.add(disposable)

                return
            }
        }

        disposable.dispose()
    }

    /**
     * See [add]
     */
    actual operator fun plusAssign(disposable: Disposable) {
        add(disposable)
    }

    /**
     * Atomically clears all the [Disposable]s
     *
     * @param dispose if true then removed [Disposable]s will be disposed, default value is true
     */
    actual fun clear(dispose: Boolean) {
        val listToDispose: List<Disposable>?

        synchronized(this) {
            listToDispose = list?.takeIf { dispose }
            list = null
        }

        listToDispose?.forEach(Disposable::dispose)
    }
}
