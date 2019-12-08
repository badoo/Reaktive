package com.badoo.reaktive.single

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotDisposed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.assertNotSuccess
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnErrorResumeNextTest :
    SingleToSingleTests by SingleToSingleTestsImpl({ onErrorResumeNext { Unit.toSingle() } }) {

    private val upstream = TestSingle<Int?>()

    @Test
    @Ignore
    override fun produces_error_WHEN_upstream_produced_error() {
        // not applicable
    }

    @Test
    fun subscribes_to_resume_next_WHEN_upstream_produced_error() {
        val (errorResumeNext, _) = createTestWithSingle()

        upstream.onError(Throwable())

        assertTrue(errorResumeNext.hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_resume_next_WHEN_upstream_produced_success() {
        val (errorResumeNext, _) = createTestWithSingle()

        upstream.onSuccess(0)

        assertFalse(errorResumeNext.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_upstream_produced_error() {
        createTestWithSingle()

        upstream.onError(Throwable())

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_resume_next_WHEN_disposed() {
        val (errorResumeNext, observer) = createTestWithSingle()

        upstream.onError(Throwable())
        observer.dispose()

        assertFalse(errorResumeNext.hasSubscribers)
    }

    @Test
    fun does_not_disposes_WHEN_upstream_produced_error_and_resume_next_did_not_emit_anything() {
        val (_, observer) = createTestWithSingle()

        upstream.onError(Throwable())

        observer.assertNotDisposed()
    }

    @Test
    fun does_not_produce_success_WHEN_upstream_produced_error_and_resume_next_did_not_produce_success() {
        val (_, observer) = createTestWithSingle()

        upstream.onError(Throwable())

        observer.assertNotSuccess()
    }

    @Test
    fun produces_success_WHEN_upstream_produced_error_and_resume_next_produced_success() {
        val (errorResumeNext, observer) = createTestWithSingle()

        upstream.onError(Throwable())
        errorResumeNext.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error_and_resume_next_produced_error() {
        val (errorResumeNext, observer) = createTestWithSingle()

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

    private fun createTestWithSingle(): Pair<TestSingle<Int?>, TestSingleObserver<Int?>> {
        val errorResumeNext = TestSingle<Int?>()
        val observer = upstream.onErrorResumeNext { errorResumeNext }.test()
        return errorResumeNext to observer
    }

}
