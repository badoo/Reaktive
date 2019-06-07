package com.badoo.reaktive.test.maybe

import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.test.maybe.TestMaybeObserver.Event

class TestMaybeObserver<T> : TestObserver<Event<T>>(), MaybeObserver<T> {

    override fun onSuccess(value: T) {
        onEvent(Event.OnSuccess(value))
    }

    override fun onComplete() {
        onEvent(Event.OnComplete)
    }

    override fun onError(error: Throwable) {
        onEvent(Event.OnError(error))
    }

    sealed class Event<out T> {
        data class OnSuccess<out T>(val value: T) : Event<T>()
        object OnComplete : Event<Nothing>()
        data class OnError(val error: Throwable) : Event<Nothing>()
    }
}