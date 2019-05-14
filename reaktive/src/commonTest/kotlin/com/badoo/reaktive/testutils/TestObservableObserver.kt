package com.badoo.reaktive.testutils

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.update

class TestObservableObserver<T> : ObservableObserver<T> {

    private val disposablesRef: AtomicReference<List<Disposable>> = AtomicReference(emptyList(), true)
    val disposables get() = disposablesRef.value
    private val eventsRef: AtomicReference<List<Event<T>>> = AtomicReference(emptyList(), true)
    val events get() = eventsRef.value

    override fun onSubscribe(disposable: Disposable) {
        disposablesRef.update { it + disposable }
    }

    override fun onNext(value: T) {
        eventsRef.update { it + Event.OnNext(value) }
    }

    override fun onComplete() {
        eventsRef.update { it + Event.OnComplete }
    }

    override fun onError(error: Throwable) {
        eventsRef.update { it + Event.OnError(error) }
    }

    fun reset() {
        eventsRef.update { emptyList() }
    }

    sealed class Event<out T> {
        data class OnNext<out T>(val value: T) : Event<T>()
        object OnComplete : Event<Nothing>()
        data class OnError(val error: Throwable) : Event<Nothing>()
    }
}
