package com.badoo.reaktive.test

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableEmitter
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.observable

class TestObservableRelay<T> : Observable<T>, ObservableEmitter<T> {

    private var emitter: ObservableEmitter<T>? = null

    private val observable =
        observable {
            check(emitter == null) { "Already subscribed" }

            emitter = it
        }

    override fun subscribe(observer: ObservableObserver<T>) {
        observable.subscribe(observer)
    }

    override val isDisposed: Boolean get() = requireNotNull(emitter).isDisposed

    override fun setDisposable(disposable: Disposable?) {
        requireNotNull(emitter).setDisposable(disposable)
    }

    override fun onNext(value: T) {
        requireNotNull(emitter).onNext(value)
    }

    override fun onComplete() {
        requireNotNull(emitter).onComplete()
    }

    override fun onError(error: Throwable) {
        requireNotNull(emitter).onError(error)
    }
}
