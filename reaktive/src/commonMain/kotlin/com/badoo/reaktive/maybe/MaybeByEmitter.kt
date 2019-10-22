package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed

fun <T> maybe(onSubscribe: (emitter: MaybeEmitter<T>) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val emitter = DisposableEmitter(observer)
        observer.onSubscribe(emitter)
        emitter.tryCatch(block = { onSubscribe(emitter) })
    }

private class DisposableEmitter<T>(
    private val observer: MaybeObserver<T>
) : DisposableWrapper(), MaybeEmitter<T> {
    override fun onSuccess(value: T) {
        doIfNotDisposed(dispose = true) {
            observer.onSuccess(value)
        }
    }

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
