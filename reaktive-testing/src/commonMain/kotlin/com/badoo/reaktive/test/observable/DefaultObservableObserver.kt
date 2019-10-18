package com.badoo.reaktive.test.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableObserver

interface DefaultObservableObserver<T> : ObservableObserver<T> {

    override fun onSubscribe(disposable: Disposable) {
    }

    override fun onNext(value: T) {
    }

    override fun onComplete() {
    }

    override fun onError(error: Throwable) {
    }
}
