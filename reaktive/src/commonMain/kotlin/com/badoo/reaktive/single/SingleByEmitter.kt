package com.badoo.reaktive.single

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed

fun <T> single(onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val emitter = DisposableEmitter(observer)
        observer.onSubscribe(emitter)
        emitter.tryCatch(block = { onSubscribe(emitter) })
    }

private class DisposableEmitter<T>(
    private val observer: SingleObserver<T>
) : DisposableWrapper(), SingleEmitter<T> {
    override fun onSuccess(value: T) {
        doIfNotDisposed(dispose = true) {
            observer.onSuccess(value)
        }
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
