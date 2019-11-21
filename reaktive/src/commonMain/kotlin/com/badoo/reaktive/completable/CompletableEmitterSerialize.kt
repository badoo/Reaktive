package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Emitter
import com.badoo.reaktive.utils.serializer.serializer

fun CompletableEmitter.serialize(): CompletableEmitter = SerializedCompletableEmitter(this)

private class SerializedCompletableEmitter(
    private val delegate: CompletableEmitter
) : CompletableEmitter, Emitter by delegate {

    private val serializer =
        serializer<Event> { event ->
            when (event) {
                Event.OnComplete -> delegate.onComplete()
                is Event.OnError -> delegate.onError(event.error)
            }

            false
        }

    override fun onComplete() {
        serializer.accept(Event.OnComplete)
    }

    override fun onError(error: Throwable) {
        serializer.accept(Event.OnError(error))
    }

    private sealed class Event {
        object OnComplete : Event()
        class OnError(val error: Throwable) : Event()
    }
}
