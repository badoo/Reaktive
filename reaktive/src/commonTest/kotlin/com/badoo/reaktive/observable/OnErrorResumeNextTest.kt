package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.isComplete
import com.badoo.reaktive.test.observable.isError
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.observable.values
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnErrorResumeNextTest :
    ObservableToObservableTests by ObservableToObservableTests<Unit>({ onErrorResumeNext { Unit.toObservable() } }) {

    private val upstream = TestObservable<Int?>()

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
    fun disposes_upstream_WHEN_upstream_produced_error() {
        createTestWithObservable()

        upstream.onError(Throwable())

        assertTrue(upstream.isDisposed)
    }

    @Test
    fun disposes_resume_next_WHEN_disposed() {
        val (errorResumeNext, observer) = createTestWithObservable()

        upstream.onError(Throwable())
        observer.dispose()

        assertTrue(errorResumeNext.isDisposed)
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_error_and_resume_next_did_not_complete() {
        val (_, observer) = createTestWithObservable()

        upstream.onError(Throwable())

        assertFalse(observer.isComplete)
    }

    @Test
    fun completes_WHEN_upstream_produced_error_and_resume_next_completed() {
        val (errorResumeNext, observer) = createTestWithObservable()

        upstream.onError(Throwable())
        errorResumeNext.onComplete()

        assertTrue(observer.isComplete)
    }

    @Test
    fun does_not_produce_values_WHEN_upstream_produced_error_and_resume_next_did_not_produce_values() {
        val (_, observer) = createTestWithObservable()

        upstream.onError(Throwable())

        assertTrue(observer.values.isEmpty())
    }

    @Test
    fun produces_values_WHEN_upstream_produced_error_and_resume_next_produced_values() {
        val (errorResumeNext, observer) = createTestWithObservable()

        upstream.onError(Throwable())
        errorResumeNext.onNext(0, 1, null)

        assertEquals(listOf(0, 1, null), observer.values)
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error_and_resume_next_produced_error() {
        val (errorResumeNext, observer) = createTestWithObservable()

        upstream.onError(Throwable())
        val throwable = Throwable()
        errorResumeNext.onError(throwable)

        assertTrue(observer.isError(throwable))
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error_and_resume_next_supplier_produced_error() {
        val throwable = Throwable()
        val observer = upstream.onErrorResumeNext { throw throwable }.test()
        upstream.onError(Throwable())

        assertTrue(observer.isError(throwable))
    }

    private fun createTestWithObservable(): Pair<TestObservable<Int?>, TestObservableObserver<Int?>> {
        val errorResumeNext = TestObservable<Int?>()
        val observer = upstream.onErrorResumeNext { errorResumeNext }.test()
        return errorResumeNext to observer
    }
}