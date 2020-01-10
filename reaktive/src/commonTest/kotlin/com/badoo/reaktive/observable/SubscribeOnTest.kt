package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscribeOnTest
    : ObservableToObservableTests by ObservableToObservableTestsImpl({ subscribeOn(TestScheduler()) }) {

    private val scheduler = TestScheduler(isManualProcessing = true)
    private val upstream = TestObservable<Int?>()
    private val observer = upstream.subscribeOn(scheduler).test()

    @Test
    fun does_not_subscribe_synchronously() {
        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun subscribes_through_scheduler() {
        scheduler.process()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun emits_values_synchronously() {
        scheduler.process()
        observer.reset()
        upstream.onNext(null)
        upstream.onNext(1)
        upstream.onNext(2)

        observer.assertValues(null, 1, 2)
    }

    @Test
    fun completes_synchronously() {
        scheduler.process()
        observer.reset()
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun errors_synchronously() {
        scheduler.process()
        val error = Throwable()
        observer.reset()
        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun disposes_executor_WHEN_disposed() {
        observer.dispose()

        scheduler.assertAllExecutorsDisposed()
    }
}
