package com.badoo.reaktive.test

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeEmitter
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.maybe.maybe
import com.badoo.reaktive.utils.atomic.AtomicReference

class TestMaybeRelay<T> : Maybe<T>, MaybeEmitter<T> {

    private val emitterRef = AtomicReference<MaybeEmitter<T>?>(null)
    private val emitter: MaybeEmitter<T> get() = requireNotNull(emitterRef.value)

    private val observable =
        maybe<T> {
            check(emitterRef.compareAndSet(null, it)) {
                "Already subscribed"
            }
        }

    override fun subscribe(observer: MaybeObserver<T>) {
        observable.subscribe(observer)
    }

    override val isDisposed: Boolean get() = emitter.isDisposed

    override fun setDisposable(disposable: Disposable?) {
        emitter.setDisposable(disposable)
    }

    override fun onSuccess(value: T) {
        emitter.onSuccess(value)
    }

    override fun onComplete() {
        emitter.onComplete()
    }

    override fun onError(error: Throwable) {
        emitter.onError(error)
    }
}
