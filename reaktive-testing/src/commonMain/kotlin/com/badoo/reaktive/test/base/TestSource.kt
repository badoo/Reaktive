package com.badoo.reaktive.test.base

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.Source
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update

open class TestSource<O> : Source<O>, Disposable, ErrorCallback where O : Observer, O : ErrorCallback {

    private val _observers: AtomicReference<List<O>> = AtomicReference(emptyList())
    val observers get() = _observers.value

    private val _isDisposed = AtomicBoolean()
    override val isDisposed: Boolean get() = _isDisposed.value

    override fun subscribe(observer: O) {
        _observers.update { it + observer }
        observer.onSubscribe(this)
    }

    override fun dispose() {
        _isDisposed.value = true
        _observers.value = emptyList()
    }

    override fun onError(error: Throwable) {
        observers.forEach { it.onError(error) }
    }

    protected inline fun onEvent(block: (O) -> Unit) {
        observers.forEach(block)
    }
}