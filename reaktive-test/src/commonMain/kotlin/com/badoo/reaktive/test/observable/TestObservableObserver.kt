package com.badoo.reaktive.test.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.update

class TestObservableObserver<T> : ObservableObserver<T> {

    private val _disposables: AtomicReference<List<Disposable>> = AtomicReference(emptyList(), true)
    val disposables get() = _disposables.value

    private val _events: AtomicReference<List<Event<T>>> = AtomicReference(emptyList(), true)
    val events get() = _events.value

    override fun onSubscribe(disposable: Disposable) {
        _disposables.update { it + disposable }
    }

    override fun onNext(value: T) {
        _events.update { it + Event.OnNext(value) }
    }

    override fun onComplete() {
        _events.update { it + Event.OnComplete }
    }

    override fun onError(error: Throwable) {
        _events.update { it + Event.OnError(error) }
    }

    fun reset() {
        _events.update { emptyList() }
    }

    sealed class Event<out T> {
        data class OnNext<out T>(val value: T) : Event<T>()
        object OnComplete : Event<Nothing>()
        data class OnError(val error: Throwable) : Event<Nothing>()
    }
}
