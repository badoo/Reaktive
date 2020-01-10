package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import kotlin.test.Test
import kotlin.test.assertTrue

class ObserveOnTest
    : ObservableToObservableTests by ObservableToObservableTestsImpl({ observeOn(TestScheduler()) }) {

    private val scheduler = TestScheduler(isManualProcessing = true)
    private val upstream = TestObservable<Int?>()
    private val observer = upstream.observeOn(scheduler).test()

    @Test
    fun subscribes_synchronously() {
        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun does_not_emit_values_synchronously() {
        upstream.onNext(null)
        upstream.onNext(1)
        upstream.onNext(2)

        observer.assertNoValues()
    }

    @Test
    fun emits_values_through_scheduler() {
        upstream.onNext(null)
        upstream.onNext(1)
        scheduler.process()
        upstream.onNext(2)
        scheduler.process()
        scheduler.process()

        observer.assertValues(null, 1, 2)
    }

    @Test
    fun does_no_complete_synchronously() {
        upstream.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun completes_through_scheduler() {
        upstream.onComplete()
        scheduler.process()

        observer.assertComplete()
    }

    @Test
    fun does_not_error_synchronously() {
        val error = Throwable()

        upstream.onError(error)

        observer.assertNotError()
    }

    @Test
    fun errors_through_scheduler() {
        val error = Throwable()

        upstream.onError(error)
        scheduler.process()

        observer.assertError(error)
    }

    @Test
    fun disposes_executor_WHEN_disposed() {
        observer.dispose()

        scheduler.assertAllExecutorsDisposed()
    }
}
