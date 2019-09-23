package com.badoo.reaktive.test.base

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.Source
import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.utils.atomic.AtomicList
import com.badoo.reaktive.utils.atomic.minusAssign
import com.badoo.reaktive.utils.atomic.plusAssign

open class TestSource<O> : Source<O>, ErrorCallback where O : Observer, O : ErrorCallback {

    private val _observers: AtomicList<O> = AtomicList(emptyList())
    val observers get() = _observers.value

    override fun subscribe(observer: O) {
        _observers += observer
        observer.onSubscribe(disposable { _observers -= observer })
    }

    override fun onError(error: Throwable) {
        observers.forEach { it.onError(error) }
    }

    protected inline fun onEvent(block: (O) -> Unit) {
        observers.forEach(block)
    }
}