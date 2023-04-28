package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class SampleTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ sample(window = 100.milliseconds, scheduler = TestScheduler()) }) {

    private val upstream = TestObservable<Int?>()

    @Test
    fun emits_last_values_WHEN_every_timeout_reached() {
        val scheduler = TestScheduler()
        val timer = scheduler.timer
        val observer = upstream.sample(window = 300.milliseconds, scheduler = scheduler).test()

        upstream.onNext(0)
        timer.advanceBy(100L)
        upstream.onNext(null)
        timer.advanceBy(100L)
        upstream.onNext(1)
        timer.advanceBy(100L)

        timer.advanceBy(100L)
        upstream.onNext(2)
        timer.advanceBy(100L)
        upstream.onNext(3)
        timer.advanceBy(100L)

        upstream.onNext(4)
        timer.advanceBy(100L)
        upstream.onNext(5)
        timer.advanceBy(100L)
        upstream.onNext(null)
        timer.advanceBy(100L)

        observer.assertValues(1, 3, null)
    }

    @Test
    fun does_not_emit_value_WHEN_upstream_produced_value_right_after_subscription() {
        val scheduler = TestScheduler(isManualProcessing = true)
        val observer = upstream.sample(window = 300.milliseconds, scheduler = scheduler).test()

        upstream.onNext(0)
        scheduler.process()

        observer.assertNoValues()
    }

    @Test
    fun does_not_emit_value_WHEN_upstream_produced_value_and_upstream_completed_and_timeout_reached() {
        val scheduler = TestScheduler()
        val observer = upstream.sample(window = 300.milliseconds, scheduler = scheduler).test()

        upstream.onNext(0)
        upstream.onComplete()
        scheduler.timer.advanceBy(1000L)

        observer.assertNoValues()
    }

    @Test
    fun does_not_emit_same_value_WHEN_timeout_reached_second_time() {
        val scheduler = TestScheduler()
        val observer = upstream.sample(window = 100.milliseconds, scheduler = scheduler).test()
        upstream.onNext(0)
        scheduler.timer.advanceBy(100L)
        observer.reset()

        scheduler.timer.advanceBy(100L)

        observer.assertNoValues()
    }
}
