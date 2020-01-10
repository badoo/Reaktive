package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscribeOnTest
    : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ subscribeOn(TestScheduler()) }) {

    private val scheduler = TestScheduler(isManualProcessing = true)
    private val upstream = TestCompletable()
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
