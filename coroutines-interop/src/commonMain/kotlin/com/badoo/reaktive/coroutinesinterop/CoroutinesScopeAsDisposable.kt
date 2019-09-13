package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

fun CoroutineScope.asDisposable(): Disposable =
    object : Disposable {
        override val isDisposed: Boolean get() = !isActive

        override fun dispose() {
            this@asDisposable.cancel()
        }
    }