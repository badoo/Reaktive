package com.badoo.reaktive.testutils

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.observable.ObservableObserver

class TestObservable<T> : Observable<T>, ObservableCallbacks<T>, Disposable {

    val observers: MutableList<ObservableObserver<T>> = arrayListOf()

    override var isDisposed: Boolean = false
        private set

    override fun dispose() {
        isDisposed = true
    }

    override fun subscribe(observer: ObservableObserver<T>) {
        observers.add(observer)
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