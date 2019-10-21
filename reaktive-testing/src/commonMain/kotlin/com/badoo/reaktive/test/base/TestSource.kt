package com.badoo.reaktive.test.base

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.Source
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update

open class TestSource<O> : Source<O>, ErrorCallback where O : Observer, O : ErrorCallback {

    private val _observers = AtomicReference<List<O>>(emptyList())
    val observers get() = _observers.value

    override fun subscribe(observer: O) {
        _observers.update { it + observer }
        observer.onSubscribe(Disposable { _observers.update { it - observer } })
    }

    override fun onError(error: Throwable) {
        observers.forEach { it.onError(error) }
    }

    protected inline fun onEvent(block: (O) -> Unit) {
        observers.forEach(block)
    }
}
