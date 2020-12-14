package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class BufferCountSkipTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ buffer(count = 1) }) {

    private val upstream = TestObservable<Int?>()

    @Test
    fun emits_lists_with_single_item_WHEN_count_1() {
        val observer = upstream.buffer(count = 1).test()

        upstream.onNext(0, null, 1, null, 2)

        observer.assertValues(listOf(0), listOf(null), listOf(1), listOf(null), listOf(2))
    }

    @Test
    fun does_not_emit_anything_WHEN_count_not_reached() {
        val observer = upstream.buffer(count = 2).test()

        upstream.onNext(0)

        observer.assertNoValues()
    }

    @Test
    fun emits_first_batch_WHEN_count_reached() {
        val observer = upstream.buffer(count = 3).test()

        upstream.onNext(0, null, 1)

        observer.assertValue(listOf(0, null, 1))
    }

    @Test
    fun does_not_emit_anything_WHEN_count_not_reached_after_first_batch() {
        val observer = upstream.buffer(count = 3).test()

        upstream.onNext(0, null, 1)
        observer.reset()
        upstream.onNext(2, null)

        observer.assertNoValues()
    }

    @Test
    fun emits_second_batch_WHEN_count_reached_after_first_batch() {
        val observer = upstream.buffer(count = 3).test()

        upstream.onNext(0, null, 1)
        observer.reset()
        upstream.onNext(2, null, 3)

        observer.assertValue(listOf(2, null, 3))
    }

    @Test
    fun emits_first_batch_WHEN_skip_set_and_count_reached() {
        val observer = upstream.buffer(count = 3, skip = 4).test()

        upstream.onNext(0, null, 1)

        observer.assertValue(listOf(0, null, 1))
    }

    @Test
    fun skips_values_WHEN_skip_set() {
        val observer = upstream.buffer(count = 3, skip = 5).test()

        upstream.onNext(0, null, 1, null, 2, null, 3, null, 4, null, 5, null, 6)

        observer.assertValues(listOf(0, null, 1), listOf(null, 3, null), listOf(5, null, 6))
    }

    @Test
    fun emits_last_buffered_values_WHEN_skip_not_set_and_upstream_completed() {
        val observer = upstream.buffer(count = 3).test()

        upstream.onNext(0, null, 1)
        observer.reset()
        upstream.onNext(2, 3)
        upstream.onComplete()

        observer.assertValues(listOf(2, 3))
    }

    @Test
    fun completes_WHEN_skip_not_set_and_buffered_values_and_upstream_completed() {
        val observer = upstream.buffer(count = 3).test()

        upstream.onNext(0, null, 1)
        observer.reset()
        upstream.onNext(2, 3)
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun emits_last_buffered_values_WHEN_skip_set_and_upstream_completed() {
        val observer = upstream.buffer(count = 3, skip = 5).test()

        upstream.onNext(0, null, 1)
        observer.reset()
        upstream.onNext(null, 2, 3, 4)
        upstream.onComplete()

        observer.assertValues(listOf(3, 4))
    }

    @Test
    fun completes_WHEN_skip_set_and_buffered_values_and_upstream_completed() {
        val observer = upstream.buffer(count = 3, skip = 5).test()

        upstream.onNext(0, null, 1)
        observer.reset()
        upstream.onNext(null, 2, 3, 4)
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_emit_buffered_values_WHEN_upstream_producer_error() {
        val observer = upstream.buffer(count = 3).test()

        upstream.onNext(0, null, 1)
        observer.reset()
        upstream.onNext(2, 3)
        upstream.onError(Exception())

        observer.assertNoValues()
    }

    @Test
    fun produces_error_WHEN_buffered_items_and_upstream_producer_error() {
        val error = Exception()
        val observer = upstream.buffer(count = 3).test()

        upstream.onNext(0, null, 1)
        observer.reset()
        upstream.onNext(2, 3)
        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun does_not_emit_anything_WHEN_completed_while_skipping() {
        val observer = upstream.buffer(count = 3, skip = 5).test()

        upstream.onNext(0, null, 1)
        observer.reset()
        upstream.onNext(2)
        upstream.onComplete()

        observer.assertNoValues()
    }

    @Test
    fun completes_WHEN_completed_while_skipping() {
        val observer = upstream.buffer(count = 3, skip = 5).test()

        upstream.onNext(0, null, 1)
        observer.reset()
        upstream.onNext(2)
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun emits_overlapping_values_WHEN_skip_is_less_than_count() {
        val observer = upstream.buffer(count = 4, skip = 2).test()

        upstream.onNext(0, 1, 2, 3, 4, 5, 6, 7)
        upstream.onComplete()

        observer.assertValues(listOf(0, 1, 2, 3), listOf(2, 3, 4, 5), listOf(4, 5, 6, 7), listOf(6, 7))
    }

    @Test
    fun emits_not_overlapping_values_WHEN_skip_is_equal_to_count() {
        val observer = upstream.buffer(count = 4, skip = 4).test()

        upstream.onNext(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        upstream.onComplete()

        observer.assertValues(listOf(0, 1, 2, 3), listOf(4, 5, 6, 7), listOf(8, 9))
    }

    @Test
    fun emits_values_with_gaps_WHEN_skip_is_more_than_count() {
        val observer = upstream.buffer(count = 4, skip = 6).test()

        upstream.onNext(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
        upstream.onComplete()

        observer.assertValues(listOf(0, 1, 2, 3), listOf(6, 7, 8, 9), listOf(12, 13))
    }

    @Test
    fun emits_all_pending_buffers_WHEN_upstream_completed() {
        val observer = upstream.buffer(count = 7, skip = 2).test()

        upstream.onNext(0, 1, 2, 3, 4, 5, 6)
        upstream.onComplete()

        observer.assertValues(listOf(0, 1, 2, 3, 4, 5, 6), listOf(2, 3, 4, 5, 6), listOf(4, 5, 6), listOf(6))
    }
}
