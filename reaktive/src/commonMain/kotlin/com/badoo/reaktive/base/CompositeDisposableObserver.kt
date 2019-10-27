package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable

internal open class CompositeDisposableObserver : CompositeDisposable(), Observer {

    override fun onSubscribe(disposable: Disposable) {
        add(disposable)
    }
}
