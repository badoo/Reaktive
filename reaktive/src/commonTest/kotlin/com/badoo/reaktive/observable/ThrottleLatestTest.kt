package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.completableOfEmpty
import com.badoo.reaktive.completable.completableOfNever
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThrottleLatestTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ throttleLatest(timeout = { completableOfNever() }) }),
    ObservableToObservableForwardTests by ObservableToObservableForwardTestsImpl({ throttleLatest(timeout = { completableOfEmpty() }) }) {

    private val upstream = TestObservable<Int?>()
    private val timeouts = Timeouts()

    @Test
    fun calls_first_timeout_with_first_value_WHEN_upstream_emitted_first_value() {
        throttleLatest()

        upstream.onNext(0)

        timeouts.assertHasTimeout(0)
    }

    @Test
    fun subscribes_to_first_timeout_WHEN_upstream_emitted_first_value() {
        throttleLatest()

        upstream.onNext(0)

        assertTrue(timeouts[0].hasSubscribers)
    }

    @Test
    fun emits_first_value_WHEN_upstream_emitted_first_value() {
        val observer = throttleLatest()

        upstream.onNext(0)

        observer.assertValue(0)
    }

    @Test
    fun does_not_emit_following_values_WHEN_upstream_emitted_multiple_values() {
        val observer = throttleLatest()

        upstream.onNext(0)
        observer.reset()
        upstream.onNext(1, null, 2, null, 3)

        observer.assertNoValues()
    }

    @Test
    fun emits_last_value_WHEN_upstream_emitted_two_values_and_timeout_completed() {
        val observer = throttleLatest()

        upstream.onNext(0, 1)
        observer.reset()
        timeouts[0].onComplete()

        observer.assertValue(1)
    }

    @Test
    fun emits_last_value_WHEN_upstream_emitted_multiple_values_and_timeout_completed() {
        val observer = throttleLatest()

        upstream.onNext(0, null, 1, null, 2)
        observer.reset()
        timeouts[0].onComplete()

        observer.assertValue(2)
    }

    @Test
    fun does_not_emit_WHEN_upstream_emitted_only_one_value_and_timeout_completed() {
        val observer = throttleLatest()

        upstream.onNext(0)
        observer.reset()
        timeouts[0].onComplete()

        observer.assertNoValues()
    }

    @Test
    fun does_not_call_second_timeout_WHEN_upstream_emitted_one_value_and_first_timeout_completed() {
        throttleLatest()

        upstream.onNext(0)
        timeouts[0].onComplete()

        timeouts.assertNoTimeout(1)
    }

    @Test
    fun does_not_call_second_timeout_WHEN_upstream_emitted_two_values() {
        throttleLatest()

        upstream.onNext(0, 1)

        timeouts.assertNoTimeout(1)
    }

    @Test
    fun calls_second_timeout_with_new_value_WHEN_upstream_emitted_one_value_and_first_timeout_completed_and_upstream_emitted_new_value() {
        throttleLatest()

        upstream.onNext(0)
        timeouts[0].onComplete()
        upstream.onNext(1)

        timeouts.assertHasTimeout(1)
    }

    @Test
    fun calls_second_timeout_with_last_value_WHEN_upstream_emitted_two_values_and_first_timeout_completed() {
        throttleLatest()

        upstream.onNext(0, 1)
        timeouts[0].onComplete()

        timeouts.assertHasTimeout(1)
    }

    @Test
    fun calls_second_timeout_with_last_value_WHEN_upstream_emitted_multiple_values_and_first_timeout_completed() {
        throttleLatest()

        upstream.onNext(0, null, 1, null, 2)
        timeouts[0].onComplete()

        timeouts.assertHasTimeout(2)
    }

    @Test
    fun subscribes_to_second_timeout_WHEN_upstream_emitted_one_value_and_first_timeout_completed_and_upstream_emitted_new_value() {
        throttleLatest()

        upstream.onNext(0)
        timeouts[0].onComplete()
        upstream.onNext(1)

        assertTrue(timeouts[1].hasSubscribers)
    }

    @Test
    fun subscribes_to_second_timeout_WHEN_upstream_emitted_two_values_and_first_timeout_completed() {
        throttleLatest()

        upstream.onNext(0, 1)
        timeouts[0].onComplete()

        assertTrue(timeouts[1].hasSubscribers)
    }

    @Test
    fun subscribes_to_second_timeout_WHEN_upstream_emitted_multiple_values_and_first_timeout_completed() {
        throttleLatest()

        upstream.onNext(0, null, 1, null, 2)
        timeouts[0].onComplete()

        assertTrue(timeouts[2].hasSubscribers)
    }

    @Test
    fun emits_new_value_WHEN_upstream_emitted_one_value_and_first_timeout_completed_and_upstream_emitted_new_value() {
        val observer = throttleLatest()

        upstream.onNext(0)
        timeouts[0].onComplete()
        observer.reset()
        upstream.onNext(1)

        observer.assertValue(1)
    }

    @Test
    fun does_not_emit_WHEN_upstream_emitted_two_values_and_first_timeout_completed_and_upstream_emitted_multiple_values() {
        val observer = throttleLatest()

        upstream.onNext(0, 1)
        timeouts[0].onComplete()
        observer.reset()
        upstream.onNext(2, null, 3, null, 4)

        observer.assertNoValues()
    }

    @Test
    fun does_not_emit_WHEN_upstream_emitted_one_value_and_first_timeout_completed_and_upstream_emitted_one_value_and_second_timeout_completed() {
        val observer = throttleLatest()

        upstream.onNext(0)
        timeouts[0].onComplete()
        upstream.onNext(1)
        observer.reset()
        timeouts[1].onComplete()

        observer.assertNoValues()
    }

    @Test
    fun emits_last_value_WHEN_upstream_emitted_one_value_and_first_timeout_completed_and_upstream_emitted_two_values_and_second_timeout_completed() {
        val observer = throttleLatest()

        upstream.onNext(0)
        timeouts[0].onComplete()
        upstream.onNext(1, 2)
        observer.reset()
        timeouts[1].onComplete()

        observer.assertValue(2)
    }

    @Test
    fun emits_last_value_WHEN_upstream_emitted_one_value_and_first_timeout_completed_and_upstream_emitted_multiple_values_and_second_timeout_completed() {
        val observer = throttleLatest()

        upstream.onNext(0)
        timeouts[0].onComplete()
        upstream.onNext(1, null, 2, null, 3)
        observer.reset()
        timeouts[1].onComplete()

        observer.assertValue(3)
    }

    @Test
    fun emits_last_value_WHEN_emitLast_is_true_and_upstream_emitted_multiple_values_and_upstream_completed() {
        val observer = throttleLatest(emitLast = true)

        upstream.onNext(0, null, 1, null, 2)
        observer.reset()
        upstream.onComplete()

        observer.assertValue(2)
    }

    @Test
    fun does_not_emit_last_value_WHEN_emitLast_is_false_and_upstream_emitted_multiple_values_and_upstream_completed() {
        val observer = throttleLatest(emitLast = false)

        upstream.onNext(0, null, 1, null, 2)
        observer.reset()
        upstream.onComplete()

        observer.assertNoValues()
    }

    @Test
    fun does_not_emit_last_value_WHEN_emitLast_is_true_and_upstream_emitted_one_value_and_upstream_completed() {
        val observer = throttleLatest(emitLast = false)

        upstream.onNext(0)
        observer.reset()
        upstream.onComplete()

        observer.assertNoValues()
    }

    @Test
    fun throttles_values_with_emitLast_true() {
        val observer = throttleLatest(emitLast = true)

        upstream.onNext(0, null, 1)
        timeouts[0].onComplete()
        upstream.onNext(2, 3)
        timeouts[1].onComplete()
        timeouts[3].onComplete()
        upstream.onNext(4, 5, null)
        upstream.onComplete()

        observer.assertValues(0, 1, 3, 4, null)
    }

    @Test
    fun throttles_values_with_emitLast_false() {
        val observer = throttleLatest(emitLast = false)

        upstream.onNext(0, null, 1)
        timeouts[0].onComplete()
        upstream.onNext(2, 3)
        timeouts[1].onComplete()
        timeouts[3].onComplete()
        upstream.onNext(4, 5, null)
        upstream.onComplete()

        observer.assertValues(0, 1, 3, 4)
    }

    @Test
    fun unsubscribes_from_timeout_WHEN_upstream_completed() {
        throttleLatest()

        upstream.onNext(0)
        upstream.onComplete()

        assertFalse(timeouts[0].hasSubscribers)
    }

    @Test
    fun unsubscribes_from_timeout_WHEN_downstream_disposed() {
        val observer = throttleLatest()

        upstream.onNext(0)
        observer.dispose()

        assertFalse(timeouts[0].hasSubscribers)
    }

    private fun throttleLatest(emitLast: Boolean = false): TestObservableObserver<Int?> =
        upstream
            .throttleLatest(timeout = timeouts::create, emitLast = emitLast)
            .test()

    private class Timeouts {
        private var map: Map<Int?, TestCompletable> by AtomicReference(emptyMap())

        fun create(item: Int?): TestCompletable {
            assertFalse(item in map)

            val timeout = TestCompletable()
            this.map += item to timeout

            return timeout
        }

        fun assertHasTimeout(item: Int?) {
            assertTrue(item in map)
        }

        fun assertNoTimeout(item: Int?) {
            assertFalse(item in map)
        }

        operator fun get(item: Int?): TestCompletable {
            assertHasTimeout(item)

            return map.getValue(item)
        }
    }
}
