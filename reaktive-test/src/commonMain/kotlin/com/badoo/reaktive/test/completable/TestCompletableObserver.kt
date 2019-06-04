package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.test.base.TestObserver

class TestCompletableObserver : TestObserver<TestCompletableObserver.Event>(), CompletableObserver {

    override fun onComplete() {
        onEvent(Event.OnSuccess)
    }

    override fun onError(error: Throwable) {
        onEvent(Event.OnError(error))
    }

    sealed class Event {
        object OnSuccess : Event()
        data class OnError(val error: Throwable) : Event()
    }
}