package com.badoo.reaktive.observable

import com.badoo.reaktive.utils.serializer.Serializer
import com.badoo.reaktive.utils.serializer.serializer

internal actual open class SerializedObservableCallbacks<in T> actual constructor(
    private val delegate: ObservableCallbacks<T>
) : ObservableCallbacks<T> {

    private val serializer: Serializer<Any?> =
        serializer {
            if (it is TerminalEvent) {
                when (it) {
                    is TerminalEvent.OnComplete -> delegate.onComplete()
                    is TerminalEvent.OnError -> delegate.onError(it.error)
                }
                false
            } else {
                @Suppress("UNCHECKED_CAST")
                delegate.onNext(it as T)
                true
            }
        }

    override fun onNext(value: T) {
        serializer.accept(value)
    }

    override fun onComplete() {
        serializer.accept(TerminalEvent.OnComplete)
    }

    override fun onError(error: Throwable) {
        serializer.accept(TerminalEvent.OnError(error))
    }

    private sealed class TerminalEvent {
        object OnComplete : TerminalEvent()
        class OnError(val error: Throwable) : TerminalEvent()
    }
}
