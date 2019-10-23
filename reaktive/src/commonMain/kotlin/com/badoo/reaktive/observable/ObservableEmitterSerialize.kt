package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Emitter
import com.badoo.reaktive.utils.serializer.serializer

fun <T> ObservableEmitter<T>.serialize(): ObservableEmitter<T> = SerializedObservableEmitter(this)

private class SerializedObservableEmitter<T>(
    private val delegate: ObservableEmitter<T>
) : ObservableEmitter<T>, Emitter by delegate {

    private val serializer =
        serializer<Any?> { event ->
            if (event is Event) {
                when (event) {
                    Event.OnComplete -> delegate.onComplete()
                    is Event.OnError -> delegate.onError(event.error)
                }

                false
            } else {
                @Suppress("UNCHECKED_CAST") // Either Event or T to avoid unnecessary allocations
                delegate.onNext(event as T)

                true
            }
        }

    override fun onNext(value: T) {
        serializer.accept(value)
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
