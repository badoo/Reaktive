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

class ThreadLocalTestNative {

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun does_not_freeze_downstream_WHEN_subscribed_to_a_freezing_upstream() {
        val upstream = completableUnsafe { it.freeze() }
        val downstreamObserver = dummyObserver()
        downstreamObserver.ensureNeverFrozen()

        upstream
            .threadLocal()
            .subscribe(downstreamObserver)
    }

    @Test
    fun calls_uncaught_exception_WHEN_upstream_completed_on_another_thread() {
        testCallsUncaughtExceptionWhenEventOccurredOnBackground(CompletableCallbacks::onComplete)
    }

    @Test
    fun calls_uncaught_exception_WHEN_upstream_produced_error_on_another_thread() {
        testCallsUncaughtExceptionWhenEventOccurredOnBackground { it.onError(Exception()) }
    }

    private fun testCallsUncaughtExceptionWhenEventOccurredOnBackground(block: (CompletableCallbacks) -> Unit) {
        val caughtException: AtomicReference<Throwable?> = AtomicReference(null)
        reaktiveUncaughtErrorHandler = { caughtException.value = it }
        val upstream = TestCompletable()

        upstream
            .threadLocal()
            .subscribe(dummyObserver())

        doInBackgroundBlocking {
            block(upstream)
        }

        assertNotNull(caughtException.value)
    }

    private fun dummyObserver(): CompletableObserver =
        object : CompletableObserver {
            override fun onSubscribe(disposable: Disposable) {
            }

            override fun onComplete() {
            }

            override fun onError(error: Throwable) {
            }
        }
}
