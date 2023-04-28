package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class ThrottleTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ throttle(100.milliseconds) }) {

    private val upstream = TestObservable<Int>()
    private val scheduler = TestScheduler()
    private val timer = scheduler.timer
    private val observer = upstream.throttle(window = 100.milliseconds, scheduler = scheduler).test()

    @Test
    fun emits_first_value_WHEN_current_time_is_0L() {
        emit(0)

        observer.assertValue(0)
    }

    @Test
    fun emits_first_value_WHEN_current_time_is_less_than_window() {
        timer.advanceBy(99L)
        emit(0)

        observer.assertValue(0)
    }

    @Test
    fun emits_first_value_WHEN_current_time_is_equals_to_window() {
        timer.advanceBy(100L)
        emit(0)

        observer.assertValue(0)
    }

    @Test
    fun emits_first_value_WHEN_current_time_is_more_than_window() {
        timer.advanceBy(101L)
        emit(0)

        observer.assertValue(0)
    }

    @Test
    fun does_not_emit_WHEN_timeout_is_not_passed() {
        emit(0)
        observer.reset()
        emit(1)
        timer.advanceBy(20L)
        emit(2)
        emit(3)
        timer.advanceBy(40L)
        emit(4)
        timer.advanceBy(39L)
        emit(5)
        emit(6)

        observer.assertNoValues()
    }

    @Test
    fun emits_WHEN_timeout_is_passed() {
        emit(0)
        timer.advanceBy(99L)
        emit(1)
        timer.advanceBy(1L)
        observer.reset()

        emit(2)

        observer.assertValue(2)
    }

    @Test
    fun emits_correct_values_WHEN_complex_series() {
        emit(0)
        timer.advanceBy(40L)
        emit(1)
        emit(2)
        timer.advanceBy(59L)
        emit(3)
        timer.advanceBy(21L)
        emit(4)
        timer.advanceBy(100L)
        emit(5)
        timer.advanceBy(99L)
        emit(6)
        timer.advanceBy(1L)
        emit(7)

        observer.assertValues(0, 4, 5, 7)
    }

    @Test
    fun emits_all_values_without_closing_window_WHEN_window_is_0() {
        scheduler.isManualProcessing = true
        val observer = upstream.throttle(window = 0.milliseconds, scheduler = scheduler).test()

        upstream.onNext(0, 1)

        observer.assertValues(0, 1)
    }

    private fun emit(value: Int) {
        upstream.onNext(value)
    }
}
