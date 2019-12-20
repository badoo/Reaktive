package com.badoo.reaktive.test

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableEmitter
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicReference

class TestCompletableRelay : Completable, CompletableEmitter {

    private val emitterRef = AtomicReference<CompletableEmitter?>(null)
    private val emitter: CompletableEmitter get() = requireNotNull(emitterRef.value)

    private val observable =
        completable {
            check(emitterRef.compareAndSet(null, it)) {
                "Already subscribed"
            }
        }

    override fun subscribe(observer: CompletableObserver) {
        observable.subscribe(observer)
    }

    override val isDisposed: Boolean get() = emitter.isDisposed

    override fun setDisposable(disposable: Disposable?) {
        emitter.setDisposable(disposable)
    }

    override fun onComplete() {
        emitter.onComplete()
    }

    override fun onError(error: Throwable) {
        emitter.onError(error)
    }
}
