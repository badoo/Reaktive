package com.badoo.reaktive.test.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.MaybeObserver

interface DefaultMaybeObserver<T> : MaybeObserver<T> {

    override fun onSubscribe(disposable: Disposable) {
    }

    override fun onSuccess(value: T) {
    }

    override fun onComplete() {
    }

    override fun onError(error: Throwable) {
    }
}
