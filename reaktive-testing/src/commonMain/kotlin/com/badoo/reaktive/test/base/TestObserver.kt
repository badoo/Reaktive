package com.badoo.reaktive.test.base

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference

open class TestObserver : Observer, Disposable, ErrorCallback {

    private val _disposable = AtomicReference<Disposable?>(null)
    val disposable: Disposable? get() = _disposable.value
    private val _error = AtomicReference<Throwable?>(null)
    val error: Throwable? get() = _error.value
    val isError: Boolean get() = error != null
    override val isDisposed: Boolean get() = _disposable.value?.isDisposed == true
    private val isDisposeCalled = AtomicBoolean()

    override fun onSubscribe(disposable: Disposable) {
        if (this.disposable != null) {
            throw IllegalStateException("Already subscribed")
        }

        _disposable.value = disposable
    }

    override fun dispose() {
        isDisposeCalled.value = true
        disposable?.dispose()
    }

    override fun onError(error: Throwable) {
        checkActive()

        _error.value = error
    }

    open fun reset() {
        _error.value = null
    }

    protected open fun checkActive() {
        checkNotNull(disposable) { "Not subscribed" }
        check(error == null) { "Already failed" }
        check(!isDisposeCalled.value) { "Already disposed" }
    }
}
