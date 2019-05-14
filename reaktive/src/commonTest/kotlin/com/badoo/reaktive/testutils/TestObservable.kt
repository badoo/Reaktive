package com.badoo.reaktive.testutils

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.update

class TestObservable<T> : Observable<T>, ObservableCallbacks<T>, Disposable {

    private val observersRef: AtomicReference<List<ObservableObserver<T>>> = AtomicReference(emptyList(), true)
    val observers get() = observersRef.value

    private val isDisposedRef = AtomicReference(false)
    override val isDisposed: Boolean get() = isDisposedRef.value

    override fun dispose() {
        isDisposedRef.value = true
    }

    override fun subscribe(observer: ObservableObserver<T>) {
        observersRef.update { it + observer }
        observer.onSubscribe(this)
    }

    override fun onNext(value: T) {
        observers.forEach { it.onNext(value) }
    }

    override fun onComplete() {
        observers.forEach(ObservableObserver<*>::onComplete)
    }

    override fun onError(error: Throwable) {
        observers.forEach { it.onError(error) }
    }
}