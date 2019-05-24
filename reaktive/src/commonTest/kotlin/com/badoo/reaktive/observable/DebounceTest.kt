package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.observable.hasOnNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.observable.values
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DebounceTest : UpstreamDownstreamGenericTests by UpstreamDownstreamGenericTests<Int>({ debounce(0L, TestScheduler()) }) {

    private val upstream = TestObservable<Int>()
    private val scheduler = TestScheduler()
    private val observer = upstream.debounce(100L, scheduler).test()

    @Test
    fun does_not_emit_WHEN_timeout_not_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(99)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun emits_WHEN_timeout_is_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(100L)

        assertEquals(listOf(1), observer.values)
    }

    @Test
    fun does_not_emit_WHEN_timeout_since_last_item_not_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(50L)
        upstream.onNext(2)
        scheduler.timer.advanceBy(99L)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun emits_WHEN_timeout_since_last_item_is_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(50L)
        upstream.onNext(2)
        scheduler.timer.advanceBy(100L)

        assertEquals(listOf(2), observer.values)
    }

    @Test
    fun emits_last_unprocessed_item_WHEN_completed() {
        upstream.onNext(1)
        upstream.onNext(2)
        upstream.onComplete()

        assertEquals(listOf(2), observer.values)
    }

    @Test
    fun does_not_emit_last_item_WHEN_already_emitted_and_completed() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(100L)
        observer.reset()
        upstream.onComplete()

        assertFalse(observer.hasOnNext)
    }
}