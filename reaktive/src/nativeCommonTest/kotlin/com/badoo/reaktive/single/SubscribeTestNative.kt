package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class ThreadLocalTestNative {

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun does_not_freeze_downstream_WHEN_subscribed_to_a_freezing_upstream() {
        val upstream = singleUnsafe<Unit> { it.freeze() }
        val downstreamObserver = dummyObserver()
        downstreamObserver.ensureNeverFrozen()

        upstream
            .threadLocal()
            .subscribe(downstreamObserver)
    }

    @Test
    fun calls_uncaught_exception_WHEN_upstream_succeeded_on_another_thread() {
        testCallsUncaughtExceptionWhenEventOccurredOnBackground { it.onSuccess(Unit) }
    }

    @Test
    fun calls_uncaught_exception_WHEN_upstream_produced_error_on_another_thread() {
        testCallsUncaughtExceptionWhenEventOccurredOnBackground { it.onError(Exception()) }
    }

    private fun testCallsUncaughtExceptionWhenEventOccurredOnBackground(block: (SingleCallbacks<Unit>) -> Unit) {
        val caughtException: AtomicReference<Throwable?> = AtomicReference(null)
        reaktiveUncaughtErrorHandler = { caughtException.value = it }
        val upstream = TestSingle<Unit>()

        upstream
            .threadLocal()
            .subscribe(dummyObserver())

        doInBackgroundBlocking {
            block(upstream)
        }

        assertNotNull(caughtException.value)
    }

    private fun dummyObserver(): SingleObserver<Unit> =
        object : SingleObserver<Unit> {
            override fun onSubscribe(disposable: Disposable) {
            }

            override fun onSuccess(value: Unit) {
            }

            override fun onError(error: Throwable) {
            }
        }
}
