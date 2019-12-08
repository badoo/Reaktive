package com.badoo.reaktive.completable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.TestCompletableObserver
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.assertNotComplete
import com.badoo.reaktive.test.completable.test
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnErrorResumeNextTest :
    CompletableToCompletableTests by CompletableToCompletableTestsImpl({ onErrorResumeNext { completableOfEmpty() } }) {

    private val upstream = TestCompletable()

    @Test
    @Ignore
    override fun produces_error_WHEN_upstream_produced_error() {
        // not applicable
    }

    @Test
    fun subscribes_to_resume_next_WHEN_upstream_produced_error() {
        val (errorResumeNext, _) = createTestWithCompletable()

        upstream.onError(Throwable())

        assertTrue(errorResumeNext.hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_resume_next_WHEN_upstream_completed() {
        val (errorResumeNext, _) = createTestWithCompletable()

        upstream.onComplete()

        assertFalse(errorResumeNext.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_upstream_produced_error() {
        createTestWithCompletable()

        upstream.onError(Throwable())

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_resume_next_WHEN_disposed() {
        val (errorResumeNext, observer) = createTestWithCompletable()

        upstream.onError(Throwable())
        observer.dispose()

        assertFalse(errorResumeNext.hasSubscribers)
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_error_and_resume_next_did_not_complete() {
        val (_, observer) = createTestWithCompletable()

        upstream.onError(Throwable())

        observer.assertNotComplete()
    }

    @Test
    fun completes_WHEN_upstream_produced_error_and_resume_next_completed() {
        val (errorResumeNext, observer) = createTestWithCompletable()

        upstream.onError(Throwable())
        errorResumeNext.onComplete()

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error_and_resume_next_produced_error() {
        val (errorResumeNext, observer) = createTestWithCompletable()

        upstream.onError(Throwable())
        val throwable = Throwable()
        errorResumeNext.onError(throwable)

        observer.assertError(throwable)
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error_and_resume_next_supplier_produced_error() {
        val upstreamThrowable = Throwable()
        val supplierThrowable = Throwable()
        val observer = upstream.onErrorResumeNext { throw supplierThrowable }.test()
        upstream.onError(upstreamThrowable)

        observer.assertError { throwable ->
            throwable is CompositeException &&
                throwable.cause1 == upstreamThrowable &&
                throwable.cause2 == supplierThrowable
        }
    }

    private fun createTestWithCompletable(): Pair<TestCompletable, TestCompletableObserver> {
        val errorResumeNext = TestCompletable()
        val observer = upstream.onErrorResumeNext { errorResumeNext }.test()
        return errorResumeNext to observer
    }

}
