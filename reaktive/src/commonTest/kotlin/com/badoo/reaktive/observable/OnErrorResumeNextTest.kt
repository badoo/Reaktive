package com.badoo.reaktive.observable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnErrorResumeNextTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ onErrorResumeNext { Unit.toObservable() } }),
    ObservableToObservableForwardTests by ObservableToObservableForwardTestsImpl({ onErrorResumeNext { observableOfNever() } }) {

    private val upstream = TestObservable<Int?>()

    @Test
    @Ignore
    override fun produces_error_WHEN_upstream_produced_error() {
        // not applicable
    }

    @Test
    fun subscribes_to_resume_next_WHEN_upstream_produced_error() {
        val (errorResumeNext, _) = createTestWithObservable()

        upstream.onError(Throwable())

        assertTrue(errorResumeNext.hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_resume_next_WHEN_upstream_produced_values() {
        val (errorResumeNext, _) = createTestWithObservable()

        upstream.onNext(0, 1, null)

        assertFalse(errorResumeNext.hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_resume_next_WHEN_upstream_completed() {
        val (errorResumeNext, _) = createTestWithObservable()

        upstream.onComplete()

        assertFalse(errorResumeNext.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_upstream_produced_error() {
        createTestWithObservable()

        upstream.onError(Throwable())

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_resume_next_WHEN_disposed() {
        val (errorResumeNext, observer) = createTestWithObservable()

        upstream.onError(Throwable())
        observer.dispose()

        assertFalse(errorResumeNext.hasSubscribers)
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_error_and_resume_next_did_not_complete() {
        val (_, observer) = createTestWithObservable()

        upstream.onError(Throwable())

        observer.assertNotComplete()
    }

    @Test
    fun completes_WHEN_upstream_produced_error_and_resume_next_completed() {
        val (errorResumeNext, observer) = createTestWithObservable()

        upstream.onError(Throwable())
        errorResumeNext.onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_produce_values_WHEN_upstream_produced_error_and_resume_next_did_not_produce_values() {
        val (_, observer) = createTestWithObservable()

        upstream.onError(Throwable())

        observer.assertNoValues()
    }

    @Test
    fun produces_values_WHEN_upstream_produced_error_and_resume_next_produced_values() {
        val (errorResumeNext, observer) = createTestWithObservable()

        upstream.onError(Throwable())
        errorResumeNext.onNext(0, 1, null)

        observer.assertValues(0, 1, null)
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error_and_resume_next_produced_error() {
        val (errorResumeNext, observer) = createTestWithObservable()

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

    private fun createTestWithObservable(): Pair<TestObservable<Int?>, TestObservableObserver<Int?>> {
        val errorResumeNext = TestObservable<Int?>()
        val observer = upstream.onErrorResumeNext { errorResumeNext }.test()
        return errorResumeNext to observer
    }
}
