package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class SubscribeTestNative {

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_without_isThreadLocal() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onComplete: () -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()

        completableUnsafe { }
            .subscribe(isThreadLocal = false, onComplete = onComplete)
    }

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_with_isThreadLocal_and_observer_is_frozen() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onComplete: () -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()

        completableUnsafe { it.freeze() }
            .subscribe(isThreadLocal = true, onComplete = onComplete)
    }

    @Test
    fun calls_uncaught_exception_WHEN_thread_local_and_completed_from_another_thread() {
        testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred(CompletableCallbacks::onComplete)
    }

    @Test
    fun calls_uncaught_exception_WHEN_thread_local_and_error_produced_from_another_thread() {
        testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred { it.onError(Exception()) }
    }

    private fun testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred(block: (CompletableCallbacks) -> Unit) {
        val caughtException: AtomicReference<Throwable?> = AtomicReference(null)
        reaktiveUncaughtErrorHandler = { caughtException.value = it }
        val upstream = TestCompletable()
        upstream.subscribe(isThreadLocal = true)

        doInBackgroundBlocking {
            block(upstream)
        }

        assertNotNull(caughtException.value)
    }
}