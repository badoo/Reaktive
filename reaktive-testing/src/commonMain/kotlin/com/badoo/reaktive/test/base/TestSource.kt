package com.badoo.reaktive.test.base

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.Source
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update

open class TestSource<O> : Source<O>, ErrorCallback where O : Observer, O : ErrorCallback {

    private val _observers = AtomicReference<List<O>>(emptyList())
    val observers: List<O> get() = _observers.value

    /**
     * Total number of subscriptions since creation or last [reset] call
     */
    val subscriptionCount: Int get() = _subscriptionCount.value
    private val _subscriptionCount = AtomicInt()

    override fun subscribe(observer: O) {
        _subscriptionCount.addAndGet(1)
        _observers.update { it + observer }
        observer.onSubscribe(Disposable { _observers.update { it - observer } })
    }

    override fun onError(error: Throwable) {
        observers.forEach { it.onError(error) }
    }

    open fun reset() {
        _observers.value = emptyList()
        _subscriptionCount.value = 0
    }

    protected inline fun onEvent(block: (O) -> Unit) {
        observers.forEach(block)
    }
}
