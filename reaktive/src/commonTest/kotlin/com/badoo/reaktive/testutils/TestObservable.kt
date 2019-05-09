package com.badoo.reaktive.testutils

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.observable.ObservableObserver

class TestObservable<T> : Observable<T>, ObservableCallbacks<T> {

    private lateinit var observer: ObservableObserver<T>

    override fun subscribe(observer: ObservableObserver<T>) {
        this.observer = observer
    }

    override fun onNext(value: T) {
        observer.onNext(value)
    }

    override fun onComplete() {
        observer.onComplete()
    }

    override fun onError(error: Throwable) {
        observer.onError(error)
    }
}