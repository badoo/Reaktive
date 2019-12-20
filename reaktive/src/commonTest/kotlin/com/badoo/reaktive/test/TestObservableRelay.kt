package com.badoo.reaktive.test

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableEmitter
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.utils.atomic.AtomicReference

class TestObservableRelay<T> : Observable<T>, ObservableEmitter<T> {

    private val emitterRef = AtomicReference<ObservableEmitter<T>?>(null)
    private val emitter: ObservableEmitter<T> get() = requireNotNull(emitterRef.value)

    private val observable =
        observable<T> {
            check(emitterRef.compareAndSet(null, it)) {
                "Already subscribed"
            }
        }

    override fun subscribe(observer: ObservableObserver<T>) {
        observable.subscribe(observer)
    }

    override val isDisposed: Boolean get() = emitter.isDisposed

    override fun setDisposable(disposable: Disposable?) {
        emitter.setDisposable(disposable)
    }

    override fun onNext(value: T) {
        emitter.onNext(value)
    }

    override fun onComplete() {
        emitter.onComplete()
    }

    override fun onError(error: Throwable) {
        emitter.onError(error)
    }
}
