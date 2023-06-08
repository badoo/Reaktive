package com.badoo.reaktive.observable

import com.badoo.reaktive.utils.serializer.DefaultSerializer

internal open class SerializedObservableCallbacks<in T>(
    private val delegate: ObservableCallbacks<T>
) : DefaultSerializer<Any?>(), ObservableCallbacks<T> {

    override fun onNext(value: T) {
        accept(value)
    }

    override fun onComplete() {
        accept(OnComplete)
    }

    override fun onError(error: Throwable) {
        accept(OnError(error))
    }

    override fun onValue(value: Any?): Boolean =
        when {
            value == null -> {
                @Suppress("UNCHECKED_CAST")
                delegate.onNext(null as T)
                true
            }

            value === OnComplete -> {
                delegate.onComplete()
                false
            }

            // Checking for class equality is faster than `is`
            value::class == OnError::class -> {
                delegate.onError((value as OnError).error)
                false
            }

            else -> {
                @Suppress("UNCHECKED_CAST")
                delegate.onNext(value as T)
                true
            }
        }

    private object OnComplete

    private class OnError(val error: Throwable)
}
