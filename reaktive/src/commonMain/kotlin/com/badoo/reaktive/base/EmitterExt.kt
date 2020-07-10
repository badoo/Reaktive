package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.Disposable

inline fun Emitter.setCancellable(crossinline cancellable: () -> Unit) {
    setDisposable(Disposable(cancellable))
}
