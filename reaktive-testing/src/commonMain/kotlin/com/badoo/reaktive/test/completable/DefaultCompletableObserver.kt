package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.disposable.Disposable

interface DefaultCompletableObserver : CompletableObserver {

    override fun onSubscribe(disposable: Disposable) {
    }

    override fun onComplete() {
    }

    override fun onError(error: Throwable) {
    }
}
