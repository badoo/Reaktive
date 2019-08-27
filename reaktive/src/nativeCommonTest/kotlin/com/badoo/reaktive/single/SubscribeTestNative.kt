package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.single.TestSingle
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.test.Test

class SubscribeTestNative {

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_without_isThreadLocal() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onError: (Throwable) -> Unit = {}
        val onSuccess: (Nothing) -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onError.ensureNeverFrozen()
        onSuccess.ensureNeverFrozen()

        TestSingle<Nothing>()
            .subscribe(isThreadLocal = false, onError = onError, onSuccess = onSuccess)
    }

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_with_isThreadLocal() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onError: (Throwable) -> Unit = {}
        val onSuccess: (Nothing) -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onError.ensureNeverFrozen()
        onSuccess.ensureNeverFrozen()

        singleUnsafe<Nothing> { it.freeze() }
            .subscribe(isThreadLocal = true, onError = onError, onSuccess = onSuccess)
    }
}