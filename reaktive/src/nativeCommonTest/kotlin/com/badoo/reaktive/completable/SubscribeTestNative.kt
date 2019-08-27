package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.completable.TestCompletable
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.test.Test

class SubscribeTestNative {

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_without_isThreadLocal() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onError: (Throwable) -> Unit = {}
        val onComplete: () -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onError.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()

        TestCompletable()
            .subscribe(isThreadLocal = false, onError = onError, onComplete = onComplete)
    }

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_with_isThreadLocal() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onError: (Throwable) -> Unit = {}
        val onComplete: () -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onError.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()

        completableUnsafe { it.freeze() }
            .subscribe(isThreadLocal = true, onError = onError, onComplete = onComplete)
    }
}