package com.badoo.reaktive.base

import com.badoo.reaktive.disposable.Disposable

interface Connectable {

    fun connect(onConnect: ((Disposable) -> Unit)? = null)
}