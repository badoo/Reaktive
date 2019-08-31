package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.maybe.TestMaybe
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
        val onSuccess: (Nothing) -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()
        onSuccess.ensureNeverFrozen()

        maybeUnsafe<Nothing> { }
            .subscribe(isThreadLocal = false, onComplete = onComplete, onSuccess = onSuccess)
    }

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_with_isThreadLocal_and_observer_is_frozen() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onComplete: () -> Unit = {}
        val onSuccess: (Nothing) -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()
        onSuccess.ensureNeverFrozen()

        maybeUnsafe<Nothing> { it.freeze() }
            .subscribe(isThreadLocal = true, onComplete = onComplete, onSuccess = onSuccess)
    }

    @Test
    fun calls_uncaught_exception_WHEN_thread_local_and_succeeded_from_another_thread() {
        testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred { it.onSuccess(Unit) }
    }

    @Test
    fun calls_uncaught_exception_WHEN_thread_local_and_completed_from_another_thread() {
        testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred(MaybeCallbacks<*>::onComplete)
    }

    @Test
    fun calls_uncaught_exception_WHEN_thread_local_and_error_produced_from_another_thread() {
        testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred { it.onError(Exception()) }
    }

    private fun testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred(block: (MaybeCallbacks<Unit>) -> Unit) {
        val caughtException: AtomicReference<Throwable?> = AtomicReference(null, true)
        reaktiveUncaughtErrorHandler = { caughtException.value = it }
        val upstream = TestMaybe<Unit>()
        upstream.subscribe(isThreadLocal = true)

        doInBackgroundBlocking {
            block(upstream)
        }

        assertNotNull(caughtException.value)
    }
}