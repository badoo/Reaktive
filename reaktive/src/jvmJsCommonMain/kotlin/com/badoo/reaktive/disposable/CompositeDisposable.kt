package com.badoo.reaktive.disposable

import kotlin.jvm.Volatile

actual class CompositeDisposable actual constructor() : Disposable {

    private var list: MutableList<Disposable>? = null
    @Volatile
    private var _isDisposed = false
    override val isDisposed: Boolean get() = _isDisposed

    override fun dispose() {
        val listToDispose: List<Disposable>?

        synchronized(this) {
            _isDisposed = true
            listToDispose = list
            list = null
        }

        listToDispose?.forEach(Disposable::dispose)
    }

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

    actual operator fun plusAssign(disposable: Disposable) {
        add(disposable)
    }

    actual fun clear(dispose: Boolean) {
        val listToDispose: List<Disposable>?

        synchronized(this) {
            listToDispose = list?.takeIf { dispose }
            list = null
        }

        listToDispose?.forEach(Disposable::dispose)
    }
}
