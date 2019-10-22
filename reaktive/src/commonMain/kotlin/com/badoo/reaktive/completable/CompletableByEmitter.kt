package com.badoo.reaktive.completable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed

fun completable(onSubscribe: (emitter: CompletableEmitter) -> Unit): Completable =
    completableUnsafe { observer ->
        val emitter = DisposableEmitter(observer)
        observer.onSubscribe(emitter)
        emitter.tryCatch(block = { onSubscribe(emitter) })
    }

private class DisposableEmitter(
    private val observer: CompletableObserver
) : DisposableWrapper(), CompletableEmitter {
    override fun onComplete() {
        doIfNotDisposed(dispose = true, block = observer::onComplete)
    }

    override fun onError(error: Throwable) {
        doIfNotDisposed(dispose = true) {
            observer.onError(error)
        }
    }

    override fun setDisposable(disposable: Disposable) {
        set(disposable)
    }
}
