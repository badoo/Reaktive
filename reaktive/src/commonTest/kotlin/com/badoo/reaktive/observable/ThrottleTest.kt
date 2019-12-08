package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.NANOS_IN_MILLI
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.clock.Clock
import kotlin.test.Test

class ThrottleTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ throttle(0L) }) {

    private val clock = TestClock()
    private val upstream = TestObservable<Int>()
    private val observer = upstream.throttle(100L, clock).test()

    @Test
    fun emits_first_value_WHEN_current_time_is_0L() {
        emit(0)

        observer.assertValue(0)
    }

    @Test
    fun emits_first_value_WHEN_current_time_is_less_than_window() {
        clock.setTime(99L)
        emit(0)

        observer.assertValue(0)
    }

    @Test
    fun emits_first_value_WHEN_current_time_is_equals_to_window() {
        clock.setTime(100L)
        emit(0)

        observer.assertValue(0)
    }

    @Test
    fun emits_first_value_WHEN_current_time_is_more_than_window() {
        clock.setTime(101L)
        emit(0)

        observer.assertValue(0)
    }

    @Test
    fun does_not_emit_WHEN_timeout_is_not_passed() {
        emit(0)
        observer.reset()
        emit(1)
        clock.setTime(20L)
        emit(2)
        emit(3)
        clock.setTime(60L)
        emit(4)
        clock.setTime(99L)
        emit(5)
        emit(6)

        observer.assertNoValues()
    }

    @Test
    fun emits_WHEN_timeout_is_passed() {
        emit(0)
        clock.setTime(99L)
        emit(1)
        clock.setTime(100L)
        observer.reset()

        emit(2)

        observer.assertValue(2)
    }

    @Test
    fun emits_correct_values_WHEN_complex_series() {
        emit(0)
        clock.setTime(40L)
        emit(1)
        emit(2)
        clock.setTime(99L)
        emit(3)
        clock.setTime(120L)
        emit(4)
        clock.setTime(220L)
        emit(5)
        clock.setTime(319L)
        emit(6)
        clock.setTime(320L)
        emit(7)

        observer.assertValues(0, 4, 5, 7)
    }

    private fun emit(value: Int) {
        upstream.onNext(value)
    }

    private class TestClock : Clock {
        private val _uptimeMillis = AtomicLong(0)

        override val uptimeMillis: Long get() = _uptimeMillis.value

        override val uptimeNanos: Long get() = _uptimeMillis.value * NANOS_IN_MILLI

        fun setTime(millis: Long) {
            _uptimeMillis.value = millis
        }
    }
}
