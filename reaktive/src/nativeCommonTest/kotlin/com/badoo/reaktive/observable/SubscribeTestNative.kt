package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.test.Test

class SubscribeTestNative {

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_without_isThreadLocal() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onError: (Throwable) -> Unit = {}
        val onComplete: () -> Unit = {}
        val onNext: (Nothing) -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onError.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()
        onNext.ensureNeverFrozen()

        TestObservable<Nothing>()
            .subscribe(isThreadLocal = false, onError = onError, onComplete = onComplete, onNext = onNext)
    }

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_with_isThreadLocal() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onError: (Throwable) -> Unit = {}
        val onComplete: () -> Unit = {}
        val onNext: (Nothing) -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onError.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()
        onNext.ensureNeverFrozen()

        observableUnsafe<Nothing> { it.freeze() }
            .subscribe(isThreadLocal = true, onError = onError, onComplete = onComplete, onNext = onNext)
    }
}