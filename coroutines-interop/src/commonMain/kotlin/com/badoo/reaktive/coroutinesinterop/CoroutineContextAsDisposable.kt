package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ensureNeverFrozen
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlin.coroutines.CoroutineContext

fun CoroutineContext.asDisposable(): Disposable =
    object : Disposable {
        init {
            ensureNeverFrozen()
        }

        override val isDisposed: Boolean get() = !isActive

        override fun dispose() {
            this@asDisposable.cancel()
        }
    }
