package com.badoo.reaktive.test.base

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update

open class TestObserver<Event> : Observer, Disposable {

    private val _disposables: AtomicReference<List<Disposable>> = AtomicReference(emptyList(), true)
    val disposables get() = _disposables.value

    private val _events: AtomicReference<List<Event>> = AtomicReference(emptyList(), true)
    val events: List<Event> get() = _events.value

    override val isDisposed: Boolean get() = disposables.all(Disposable::isDisposed)

    override fun onSubscribe(disposable: Disposable) {
        _disposables.update { it + disposable }
    }

    override fun dispose() {
        disposables.forEach(Disposable::dispose)
    }

    fun reset() {
        _events.update { emptyList() }
    }

    protected fun onEvent(event: Event) {
        _events.update { it + event }
    }
}