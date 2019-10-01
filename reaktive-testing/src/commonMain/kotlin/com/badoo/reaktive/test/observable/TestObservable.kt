package com.badoo.reaktive.test.observable

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.test.base.TestSource
import com.badoo.reaktive.utils.freeze

class TestObservable<T> : TestSource<ObservableObserver<T>>(), Observable<T>, ObservableCallbacks<T> {

    init {
        freeze()
    }

    override fun onNext(value: T) {
        onEvent { it.onNext(value) }
    }

    override fun onComplete() {
        onEvent(ObservableObserver<*>::onComplete)
    }
}