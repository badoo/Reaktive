package com.badoo.reaktive.test

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleEmitter
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.single
import com.badoo.reaktive.utils.atomic.AtomicReference

class TestSingleRelay<T> : Single<T>, SingleEmitter<T> {

    private val emitterRef = AtomicReference<SingleEmitter<T>?>(null)
    private val emitter: SingleEmitter<T> get() = requireNotNull(emitterRef.value)

    private val single =
        single<T> {
            check(emitterRef.compareAndSet(null, it)) {
                "Already subscribed"
            }
        }

    override fun subscribe(observer: SingleObserver<T>) {
        single.subscribe(observer)
    }

    override val isDisposed: Boolean get() = emitter.isDisposed

    override fun setDisposable(disposable: Disposable?) {
        emitter.setDisposable(disposable)
    }

    override fun onSuccess(value: T) {
        emitter.onSuccess(value)
    }

    override fun onError(error: Throwable) {
        emitter.onError(error)
    }
}
