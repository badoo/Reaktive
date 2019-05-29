package com.badoo.reaktive.test.observable

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.test.observable.TestObservableObserver.Event
import com.badoo.reaktive.test.base.TestObserver

class TestObservableObserver<T> : TestObserver<Event<T>>(), ObservableObserver<T> {

    override fun onNext(value: T) {
        onEvent(Event.OnNext(value))
    }

    override fun onComplete() {
        onEvent(Event.OnComplete)
    }

    override fun onError(error: Throwable) {
        onEvent(Event.OnError(error))
    }

    sealed class Event<out T> {
        data class OnNext<out T>(val value: T) : Event<T>()
        object OnComplete : Event<Nothing>()
        data class OnError(val error: Throwable) : Event<Nothing>()
    }
}
