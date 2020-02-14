package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ensureNeverFrozen
import kotlinx.coroutines.Job

fun Job.asDisposable(): Disposable =
    object : Disposable {
        init {
            ensureNeverFrozen()
        }

        override val isDisposed: Boolean get() = !isActive

        override fun dispose() {
            cancel()
        }
    }
