package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.observable.*
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WindowBySignalTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ window(TestObservable<Unit>(), { TestCompletable() }) }) {

    private val upstream = TestObservable<Int?>()
    private val opening = TestObservable<Int>()
    private val closings = Closings()

    @Test
    fun emits_all_windows_correctly_WHEN_restartOnLimit_is_false_and_windows_not_abandoned() {
        val observer = window(limit = 5, restartOnLimit = false)

        val windowCount1 = observer.values.size
        upstream.onNext(0, null, 1)
        val windowCount2 = observer.values.size
        opening.onNext(0)
        val windowCount3 = observer.values.size
        upstream.onNext(2, null, 3)
        val windowCount4 = observer.values.size
        opening.onNext(1)
        val windowCount5 = observer.values.size
        upstream.onNext(4, 5)
        val windowCount6 = observer.values.size
        upstream.onNext(6, 7)
        val windowCount7 = observer.values.size
        closings[1].onComplete()
        val windowCount8 = observer.values.size
        upstream.onNext(8)
        val windowCount9 = observer.values.size

        assertEquals(0, windowCount1)
        assertEquals(0, windowCount2)
        assertEquals(1, windowCount3)
        assertEquals(1, windowCount4)
        assertEquals(2, windowCount5)
        assertEquals(2, windowCount6)
        assertEquals(2, windowCount7)
        assertEquals(2, windowCount8)
        assertEquals(2, windowCount9)
    }

    @Test
    fun emits_all_windows_correctly_WHEN_restartOnLimit_is_true_and_windows_not_abandoned() {
        val observer = window(limit = 5, restartOnLimit = true)

        val windowCount1 = observer.values.size
        upstream.onNext(0, null, 1)
        val windowCount2 = observer.values.size
        opening.onNext(0)
        val windowCount3 = observer.values.size
        upstream.onNext(2, null, 3)
        val windowCount4 = observer.values.size
        opening.onNext(1)
        val windowCount5 = observer.values.size
        upstream.onNext(4, 5)
        val windowCount6 = observer.values.size
        upstream.onNext(6, 7)
        val windowCount7 = observer.values.size
        closings[1].onComplete()
        val windowCount8 = observer.values.size
        upstream.onNext(8)
        val windowCount9 = observer.values.size

        assertEquals(0, windowCount1)
        assertEquals(0, windowCount2)
        assertEquals(1, windowCount3)
        assertEquals(1, windowCount4)
        assertEquals(2, windowCount5)
        assertEquals(3, windowCount6)
        assertEquals(3, windowCount7)
        assertEquals(3, windowCount8)
        assertEquals(3, windowCount9)
    }

    @Test
    fun emits_all_windows_correctly_WHEN_restartOnLimit_is_false_and_windows_abandoned() {
        val observer = window(limit = 5, restartOnLimit = false) {}

        val windowCount1 = observer.values.size
        upstream.onNext(0, null, 1)
        val windowCount2 = observer.values.size
        opening.onNext(0)
        val windowCount3 = observer.values.size
        upstream.onNext(2, null, 3)
        val windowCount4 = observer.values.size
        opening.onNext(1)
        val windowCount5 = observer.values.size
        upstream.onNext(4, 5)
        val windowCount6 = observer.values.size
        upstream.onNext(6, 7)
        val windowCount7 = observer.values.size
        upstream.onNext(8)
        val windowCount8 = observer.values.size

        assertEquals(0, windowCount1)
        assertEquals(0, windowCount2)
        assertEquals(1, windowCount3)
        assertEquals(1, windowCount4)
        assertEquals(2, windowCount5)
        assertEquals(2, windowCount6)
        assertEquals(2, windowCount7)
        assertEquals(2, windowCount8)
    }

    @Test
    fun emits_all_windows_correctly_WHEN_restartOnLimit_is_true_and_windows_abandoned() {
        val observer = window(limit = 5, restartOnLimit = true) {}

        val windowCount1 = observer.values.size
        upstream.onNext(0, null, 1)
        val windowCount2 = observer.values.size
        opening.onNext(0)
        val windowCount3 = observer.values.size
        upstream.onNext(2, null, 3)
        val windowCount4 = observer.values.size
        opening.onNext(1)
        val windowCount5 = observer.values.size
        upstream.onNext(4, 5)
        val windowCount6 = observer.values.size
        upstream.onNext(6, 7)
        val windowCount7 = observer.values.size
        upstream.onNext(8)
        val windowCount8 = observer.values.size

        assertEquals(0, windowCount1)
        assertEquals(0, windowCount2)
        assertEquals(1, windowCount3)
        assertEquals(1, windowCount4)
        assertEquals(2, windowCount5)
        assertEquals(2, windowCount6)
        assertEquals(2, windowCount7)
        assertEquals(2, windowCount8)
    }

    @Test
    fun all_windows_emit_correct_values_WHEN_restartOnLimit_is_false_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = false)

        upstream.onNext(0, null, 1)
        opening.onNext(0)
        upstream.onNext(2, null, 3)
        opening.onNext(1)
        upstream.onNext(4, 5)
        upstream.onNext(6, 7)
        closings[1].onComplete()
        upstream.onNext(8)

        observer.values[0].assertValues(2, null, 3, 4, 5)
        observer.values[1].assertValues(4, 5, 6, 7)
    }

    @Test
    fun all_windows_emit_correct_values_WHEN_restartOnLimit_is_true_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1)
        opening.onNext(0)
        upstream.onNext(2, null, 3)
        opening.onNext(1)
        upstream.onNext(4, 5)
        upstream.onNext(6, 7)
        closings[1].onComplete()
        upstream.onNext(8)

        observer.values[0].assertValues(2, null, 3, 4, 5)
        observer.values[1].assertValues(4, 5, 6, 7)
        observer.values[2].assertValues(6, 7, 8)
    }

    @Test
    fun closes_all_windows_correctly_WHEN_restartOnLimit_is_false_and_closing_signals() {
        val observer = window(limit = 5, restartOnLimit = false)

        opening.onNext(0)
        val statuses1 = observer.values.map { it.isComplete }
        upstream.onNext(0, null, 1)
        val statuses2 = observer.values.map { it.isComplete }
        opening.onNext(1)
        val statuses3 = observer.values.map { it.isComplete }
        upstream.onNext(2, 3)
        val statuses4 = observer.values.map { it.isComplete }
        closings[1].onComplete()
        val statuses5 = observer.values.map { it.isComplete }
        opening.onNext(2)
        val statuses6 = observer.values.map { it.isComplete }
        upstream.onNext(4, 5)
        val statuses7 = observer.values.map { it.isComplete }
        closings[2].onComplete()
        val statuses8 = observer.values.map { it.isComplete }

        assertEquals(listOf(false), statuses1)
        assertEquals(listOf(false), statuses2)
        assertEquals(listOf(false, false), statuses3)
        assertEquals(listOf(true, false), statuses4)
        assertEquals(listOf(true, true), statuses5)
        assertEquals(listOf(true, true, false), statuses6)
        assertEquals(listOf(true, true, false), statuses7)
        assertEquals(listOf(true, true, true), statuses8)
    }

    @Test
    fun closes_all_windows_correctly_WHEN_restartOnLimit_is_true_and_closing_signals() {
        val observer = window(limit = 5, restartOnLimit = true)

        opening.onNext(0)
        val statuses1 = observer.values.map { it.isComplete }
        upstream.onNext(0, null, 1)
        val statuses2 = observer.values.map { it.isComplete }
        opening.onNext(1)
        val statuses3 = observer.values.map { it.isComplete }
        upstream.onNext(2, 3)
        val statuses4 = observer.values.map { it.isComplete }
        closings[1].onComplete()
        val statuses5 = observer.values.map { it.isComplete }
        upstream.onNext(4, 5)
        val statuses6 = observer.values.map { it.isComplete }
        closings[0].onComplete()
        val statuses7 = observer.values.map { it.isComplete }

        assertEquals(listOf(false), statuses1)
        assertEquals(listOf(false), statuses2)
        assertEquals(listOf(false, false), statuses3)
        assertEquals(listOf(true, false, false), statuses4)
        assertEquals(listOf(true, true, false), statuses5)
        assertEquals(listOf(true, true, false), statuses6)
        assertEquals(listOf(true, true, true), statuses7)
    }

    @Test
    fun abandoned_window_completes_WHEN_subscribed() {
        val observer = window {}
        opening.onNext(0)

        val window = observer.values.first().test()

        window.assertComplete()
    }

    @Test
    fun subscribes_to_opening_WHEN_subscribed() {
        window()

        assertTrue(opening.hasSubscribers)
    }

    @Test
    fun does_not_emit_windows_WHEN_closings_completed() {
        val observer = window()
        opening.onNext(0, 1)
        observer.reset()

        closings[0].onComplete()
        closings[1].onComplete()

        observer.assertNoValues()
    }

    @Test
    fun window_produces_error_WHEN_subscribed_second_time() {
        val observer = window { it.test() }
        opening.onNext(0)

        val windowObserver = observer.lastValue().test()

        windowObserver.assertError()
    }

    @Test
    fun unsubscribes_from_opening_WHEN_opening_not_emitted_and_upstream_completed() {
        window()

        upstream.onComplete()

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_opening_WHEN_opening_emitted_multiple_values_and_upstream_completed_and_not_all_closings_completed() {
        window()

        opening.onNext(0, 1, 2)
        upstream.onComplete()
        closings[0].onComplete()
        closings[2].onComplete()

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_opening_WHEN_opening_emitted_multiple_values_and_upstream_completed_and_last_closing_completed() {
        window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[1].onComplete()
        upstream.onComplete()
        closings[2].onComplete()

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_opening_WHEN_opening_emitted_multiple_values_and_last_closing_completed_and_upstream_completed() {
        window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[1].onComplete()
        closings[2].onComplete()
        upstream.onComplete()

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_all_closings_WHEN_upstream_completed() {
        window()

        opening.onNext(0, 1, 2)
        upstream.onComplete()

        assertFalse(closings[0].hasSubscribers)
        assertFalse(closings[1].hasSubscribers)
        assertFalse(closings[2].hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_upstream_WHEN_opening_emitted_multiple_values_and_opening_completed_and_not_all_closings_completed() {
        window()

        opening.onNext(0, 1, 2)
        opening.onComplete()
        closings[0].onComplete()
        closings[2].onComplete()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_opening_emitted_multiple_values_and_opening_completed_and_last_closing_completed() {
        window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[1].onComplete()
        opening.onComplete()
        closings[2].onComplete()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_opening_emitted_multiple_values_and_last_closing_completed_and_opening_completed() {
        window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[1].onComplete()
        closings[2].onComplete()
        opening.onComplete()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_opening_not_emitted_and_downstream_disposed() {
        val observer = window()

        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_opening_WHEN_opening_not_emitted_and_downstream_disposed() {
        val observer = window()

        observer.dispose()

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_opening_WHEN_opening_emitted_multiple_values_and_downstream_disposed_and_not_all_closings_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[2].onComplete()
        observer.dispose()

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_opening_WHEN_opening_emitted_multiple_values_and_downstream_disposed_and_last_closing_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[1].onComplete()
        observer.dispose()
        closings[2].onComplete()

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_opening_WHEN_opening_emitted_multiple_values_and_last_closing_completed_and_downstream_disposed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[1].onComplete()
        closings[2].onComplete()
        observer.dispose()

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_closings_WHEN_opening_emitted_multiple_values_and_downstream_disposed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        observer.dispose()

        assertTrue(closings[0].hasSubscribers)
        assertTrue(closings[1].hasSubscribers)
        assertTrue(closings[2].hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_upstream_WHEN_opening_emitted_multiple_values_and_downstream_disposed_and_not_all_closings_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        observer.dispose()
        closings[0].onComplete()
        closings[2].onComplete()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_opening_emitted_multiple_values_and_downstream_disposed_and_last_closing_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[1].onComplete()
        observer.dispose()
        closings[2].onComplete()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_opening_emitted_multiple_values_and_last_closing_completed_and_downstream_disposed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[1].onComplete()
        closings[2].onComplete()
        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun windows_do_not_complete_WHEN_opening_emitted_multiple_values_and_downstream_disposed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        observer.dispose()

        observer.values[0].assertNotComplete()
        observer.values[1].assertNotComplete()
        observer.values[2].assertNotComplete()
    }

    @Test
    fun windows_do_not_complete_WHEN_opening_emitted_multiple_values_and_opening_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        opening.onComplete()

        observer.values[0].assertNotComplete()
        observer.values[1].assertNotComplete()
        observer.values[2].assertNotComplete()
    }

    @Test
    fun windows_complete_WHEN_opening_emitted_multiple_values_and_upstream_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        upstream.onComplete()

        observer.values[0].assertComplete()
        observer.values[1].assertComplete()
        observer.values[2].assertComplete()
    }

    @Test
    fun closed_windows_complete_WHEN_opening_emitted_multiple_values_and_corresponding_closings_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[2].onComplete()

        observer.values[0].assertComplete()
        observer.values[2].assertComplete()
    }

    @Test
    fun not_closed_windows_do_not_complete_WHEN_opening_emitted_multiple_values_and_some_closings_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[2].onComplete()

        observer.values[1].assertNotComplete()
    }

    @Test
    fun windows_produce_errors_WHEN_opening_emitted_multiple_values_and_upstream_produced_error() {
        val observer = window()
        val error = Exception()

        opening.onNext(0, 1, 2)
        upstream.onError(error)

        observer.values[0].assertError(error)
        observer.values[1].assertError(error)
        observer.values[2].assertError(error)
    }

    @Test
    fun downstream_produces_error_WHEN_upstream_produced_error() {
        val observer = window()
        val error = Exception()

        opening.onNext(0, 1, 2)
        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun windows_produce_errors_WHEN_opening_emitted_multiple_values_and_opening_produced_error() {
        val observer = window()
        val error = Exception()

        opening.onNext(0, 1, 2)
        opening.onError(error)

        observer.values[0].assertError(error)
        observer.values[1].assertError(error)
        observer.values[2].assertError(error)
    }

    @Test
    fun downstream_produces_error_WHEN_opening_not_emitted_and_opening_produced_error() {
        val observer = window()
        val error = Exception()

        opening.onError(error)

        observer.assertError(error)
    }

    @Test
    fun downstream_produces_error_WHEN_opening_emitted_multiple_values_and_opening_produced_error() {
        val observer = window()
        val error = Exception()

        opening.onNext(0, 1, 2)
        opening.onError(error)

        observer.assertError(error)
    }

    @Test
    fun windows_produce_errors_WHEN_opening_emitted_multiple_values_and_closing_produced_error() {
        val observer = window()
        val error = Exception()

        opening.onNext(0, 1, 2)
        closings[0].onError(error)

        observer.values[0].assertError(error)
        observer.values[1].assertError(error)
        observer.values[2].assertError(error)
    }

    @Test
    fun downstream_produces_error_WHEN_opening_emitted_multiple_values_and_closing_produced_error() {
        val observer = window()
        val error = Exception()

        opening.onNext(0, 1, 2)
        closings[0].onError(error)

        observer.assertError(error)
    }

    @Test
    fun unsubscribes_from_closings_WHEN_opening_emitted_multiple_values_and_upstream_produced_error() {
        window()

        opening.onNext(0, 1, 2)
        upstream.onError(Exception())

        assertFalse(closings[0].hasSubscribers)
        assertFalse(closings[1].hasSubscribers)
        assertFalse(closings[2].hasSubscribers)
    }

    @Test
    fun unsubscribes_from_opening_WHEN_upstream_produced_error() {
        window()

        upstream.onError(Exception())

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_closings_WHEN_opening_emitted_multiple_values_and_opening_produced_error() {
        window()

        opening.onNext(0, 1, 2)
        opening.onError(Exception())

        assertFalse(closings[0].hasSubscribers)
        assertFalse(closings[1].hasSubscribers)
        assertFalse(closings[2].hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_opening_produced_error() {
        window()

        opening.onError(Exception())

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_opening_emitted_multiple_values_and_closing_produced_error() {
        window()

        opening.onNext(0, 1, 2)
        closings[1].onError(Exception())

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_opening_WHEN_opening_emitted_multiple_values_and_closing_produced_error() {
        window()

        opening.onNext(0, 1, 2)
        closings[1].onError(Exception())

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_closings_WHEN_opening_emitted_multiple_values_and_closing_produced_error() {
        window()

        opening.onNext(0, 1, 2)
        closings[1].onError(Exception())

        assertFalse(closings[0].hasSubscribers)
        assertFalse(closings[1].hasSubscribers)
        assertFalse(closings[2].hasSubscribers)
    }

    @Test
    fun does_not_complete_WHEN_opening_emitted_multiple_values_and_opening_completed_and_not_all_closings_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        opening.onComplete()
        closings[0].onComplete()
        closings[2].onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun completes_WHEN_opening_emitted_multiple_values_and_last_closing_completed_and_opening_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[1].onComplete()
        closings[2].onComplete()
        opening.onComplete()

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_opening_emitted_multiple_values_and_opening_completed_and_last_closing_completed() {
        val observer = window()

        opening.onNext(0, 1, 2)
        closings[0].onComplete()
        closings[1].onComplete()
        opening.onComplete()
        closings[2].onComplete()

        observer.assertComplete()
    }

    @Test
    fun downstream_produces_error_WHEN_closing_supplier_thrown() {
        val error = Exception()
        val observer = window(closing = { throw error })

        opening.onNext(0)

        observer.assertError(error)
    }

    @Test
    fun windows_produce_errors_WHEN_opening_emitted_multiple_values_and_closing_supplier_thrown() {
        val error = Exception()
        val observer = window(closing = { if (it < 2) closings.create(it) else throw error })

        opening.onNext(0, 1, 2)

        observer.values[0].assertError(error)
        observer.values[1].assertError(error)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_closing_supplier_thrown() {
        window(closing = { throw Exception() })

        opening.onNext(0)

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_opening_WHEN_closing_supplier_thrown() {
        window(closing = { throw Exception() })

        opening.onNext(0)

        assertFalse(opening.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_closings_WHEN_closing_supplier_thrown() {
        window(closing = { if (it < 2) closings.create(it) else throw Exception() })

        opening.onNext(0, 1, 2)

        assertFalse(closings[0].hasSubscribers)
        assertFalse(closings[1].hasSubscribers)
    }

    private fun window(
        closing: (Int) -> Completable = closings::create,
        limit: Long = Long.MAX_VALUE,
        restartOnLimit: Boolean = false,
        onNext: (Observable<Int?>) -> Unit
    ): TestObservableObserver<Observable<Int?>> =
        upstream
            .window(opening = opening, closing = closing, limit = limit, restartOnLimit = restartOnLimit)
            .doOnBeforeNext(onNext)
            .test()

    private fun window(
        closing: (Int) -> Completable = closings::create,
        limit: Long = Long.MAX_VALUE,
        restartOnLimit: Boolean = false
    ): TestObservableObserver<TestObservableObserver<Int?>> =
        upstream
            .window(opening = opening, closing = closing, limit = limit, restartOnLimit = restartOnLimit)
            .map { it.test() }
            .test()

    private fun <T> TestObservableObserver<T>.lastValue(): T = values.last()

    private class Closings {
        private val map = AtomicReference(emptyMap<Int, TestCompletable>())

        fun create(value: Int): TestCompletable {
            assertFalse(value in map.value)
            val closing = TestCompletable()
            map.update { it + (value to closing) }

            return closing
        }

        operator fun get(value: Int): TestCompletable {
            assertTrue(value in map.value)

            return map.value.getValue(value)
        }
    }
}
