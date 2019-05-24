package com.badoo.reaktive.test.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.update

class TestObservable<T> : Observable<T>, ObservableCallbacks<T>, Disposable {

    private val _observers: AtomicReference<List<ObservableObserver<T>>> = AtomicReference(emptyList(), true)
    val observers get() = _observers.value

    private val _isDisposed = AtomicReference(false)
    override val isDisposed: Boolean get() = _isDisposed.value

    override fun dispose() {
        _isDisposed.value = true
        _observers.value = emptyList()
    }

    override fun subscribe(observer: ObservableObserver<T>) {
        _observers.update { it + observer }
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