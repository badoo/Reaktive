package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Emitter
import com.badoo.reaktive.utils.serializer.DefaultSerializer

fun CompletableEmitter.serialize(): CompletableEmitter = SerializedCompletableEmitter(this)

private class SerializedCompletableEmitter(
    private val delegate: CompletableEmitter
) : DefaultSerializer<SerializedCompletableEmitter.Event>(), CompletableEmitter, Emitter by delegate {

    override fun onValue(value: Event): Boolean {
        when (value) {
            Event.OnComplete -> delegate.onComplete()
            is Event.OnError -> delegate.onError(value.error)
        }

        return false
    }

    override fun onComplete() {
        accept(Event.OnComplete)
    }

    override fun onError(error: Throwable) {
        accept(Event.OnError(error))
    }

    sealed class Event {
        object OnComplete : Event()
        class OnError(val error: Throwable) : Event()
    }
}
