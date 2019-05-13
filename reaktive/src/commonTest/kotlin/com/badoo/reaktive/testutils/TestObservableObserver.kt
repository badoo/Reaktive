package com.badoo.reaktive.testutils

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableObserver

class TestObservableObserver<T> : ObservableObserver<T> {

    val disposables = arrayListOf<Disposable>()
    val events = arrayListOf<Event<T>>()

    override fun onSubscribe(disposable: Disposable) {
        disposables.add(disposable)
    }

    override fun onNext(value: T) {
        events.add(Event.OnNext(value))
    }

    override fun onComplete() {
        events.add(Event.OnComplete)
    }

    override fun onError(error: Throwable) {
        events.add(Event.OnError(error))
    }

    sealed class Event<out T> {
        data class OnNext<out T>(val value: T) : Event<T>()
        object OnComplete : Event<Nothing>()
        data class OnError(val error: Throwable) : Event<Nothing>()
    }
}
