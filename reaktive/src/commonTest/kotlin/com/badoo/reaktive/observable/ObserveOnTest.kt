package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.hasOnNext
import com.badoo.reaktive.test.observable.isCompleted
import com.badoo.reaktive.test.observable.isError
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.observable.values
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObserveOnTest
    : ObservableToObservableTests by ObservableToObservableTests<Unit>({ observeOn(TestScheduler()) }) {

    private val scheduler = TestScheduler(isManualProcessing = true)
    private val upstream = TestObservable<Int>()
    private val observer = upstream.observeOn(scheduler).test()

    @Test
    fun subscribes_synchronously() {
        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun does_not_emit_values_synchronously() {
        upstream.onNext(0)
        upstream.onNext(1)
        upstream.onNext(2)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun emits_values_through_scheduler() {
        upstream.onNext(0)
        upstream.onNext(1)
        scheduler.process()
        upstream.onNext(2)
        scheduler.process()
        scheduler.process()

        assertEquals(listOf(0, 1, 2), observer.values)
    }

    @Test
    fun does_no_complete_synchronously() {
        upstream.onComplete()

        assertFalse(observer.isCompleted)
    }

    @Test
    fun completes_through_scheduler() {
        upstream.onComplete()
        scheduler.process()

        assertTrue(observer.isCompleted)
    }

    @Test
    fun does_not_error_synchronously() {
        upstream.onError(Throwable())

        assertFalse(observer.isError)
    }

    @Test
    fun errors_through_scheduler() {
        val error = Throwable()
        upstream.onError(error)
        scheduler.process()

        assertTrue(observer.isError(error))
    }

    @Test
    fun disposes_executor_WHEN_disposed() {
        observer.dispose()

        assertTrue(scheduler.executors.all(TestScheduler.Executor::isDisposed))
    }
}