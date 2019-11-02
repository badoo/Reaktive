package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

@PublishedApi
internal open class DisposableEmitter : DisposableWrapper(), Emitter {

    override fun setDisposable(disposable: Disposable) {
        set(disposable)
    }
}
