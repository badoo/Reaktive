package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.Disposable
import kotlinx.coroutines.Job

fun Job.asDisposable(): Disposable =
    object : Disposable {
        override val isDisposed: Boolean get() = !isActive

        override fun dispose() {
            cancel()
        }
    }
