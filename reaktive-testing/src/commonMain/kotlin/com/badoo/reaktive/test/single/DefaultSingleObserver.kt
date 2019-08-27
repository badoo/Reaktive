package com.badoo.reaktive.test.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.SingleObserver

interface DefaultSingleObserver<T> : SingleObserver<T> {

    override fun onSubscribe(disposable: Disposable) {
    }

    override fun onSuccess(value: T) {
    }

    override fun onError(error: Throwable) {
    }
}