package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.maybe.TestMaybe
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.test.Test

class SubscribeTestNative {

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_without_isThreadLocal() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onError: (Throwable) -> Unit = {}
        val onComplete: () -> Unit = {}
        val onSuccess: (Nothing) -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onError.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()
        onSuccess.ensureNeverFrozen()

        TestMaybe<Nothing>()
            .subscribe(isThreadLocal = false, onError = onError, onComplete = onComplete, onSuccess = onSuccess)
    }

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_with_isThreadLocal() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onError: (Throwable) -> Unit = {}
        val onComplete: () -> Unit = {}
        val onSuccess: (Nothing) -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onError.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()
        onSuccess.ensureNeverFrozen()

        maybeUnsafe<Nothing> { it.freeze() }
            .subscribe(isThreadLocal = true, onError = onError, onComplete = onComplete, onSuccess = onSuccess)
    }
}