package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.test.single.TestSingleObserver.Event

class TestSingleObserver<T> : TestObserver<Event<T>>(), SingleObserver<T> {

    override fun onSuccess(value: T) {
        onEvent(Event.OnSuccess(value))
    }

    override fun onError(error: Throwable) {
        onEvent(Event.OnError(error))
    }

    sealed class Event<out T> {
        data class OnSuccess<out T>(val value: T) : Event<T>()
        data class OnError(val error: Throwable) : Event<Nothing>()
    }
}
