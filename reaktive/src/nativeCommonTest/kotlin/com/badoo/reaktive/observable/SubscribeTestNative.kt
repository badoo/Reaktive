package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
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
        val onNext: (Nothing) -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()
        onNext.ensureNeverFrozen()

        observableUnsafe<Nothing> { }
            .subscribe(isThreadLocal = false, onComplete = onComplete, onNext = onNext)
    }

    @Test
    fun does_not_freeze_callbacks_WHEN_subscribed_with_isThreadLocal_and_observer_is_frozen() {
        val onSubscribe: (Disposable) -> Unit = {}
        val onComplete: () -> Unit = {}
        val onNext: (Nothing) -> Unit = {}
        onSubscribe.ensureNeverFrozen()
        onComplete.ensureNeverFrozen()
        onNext.ensureNeverFrozen()

        observableUnsafe<Nothing> { it.freeze() }
            .subscribe(isThreadLocal = true, onComplete = onComplete, onNext = onNext)
    }

    @Test
    fun calls_uncaught_exception_WHEN_thread_local_and_value_produced_from_another_thread() {
        testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred { it.onNext(Unit) }
    }

    @Test
    fun calls_uncaught_exception_WHEN_thread_local_and_completed_from_another_thread() {
        testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred(ObservableCallbacks<*>::onComplete)
    }

    @Test
    fun calls_uncaught_exception_WHEN_thread_local_and_error_produced_from_another_thread() {
        testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred { it.onError(Exception()) }
    }

    private fun testCallsUncaughtExceptionWhenThreadLocalAndEventOccurred(block: (ObservableCallbacks<Unit>) -> Unit) {
        val caughtException: AtomicReference<Throwable?> = AtomicReference(null)
        reaktiveUncaughtErrorHandler = { caughtException.value = it }
        val upstream = TestObservable<Unit>()
        upstream.subscribe(isThreadLocal = true)

        doInBackgroundBlocking {
            block(upstream)
        }

        assertNotNull(caughtException.value)
    }
}