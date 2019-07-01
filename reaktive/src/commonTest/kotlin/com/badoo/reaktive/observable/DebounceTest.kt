package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test

class DebounceTest
    : ObservableToObservableTests by ObservableToObservableTests<Int>({ debounce(0L, TestScheduler()) }) {

    private val upstream = TestObservable<Int>()
    private val scheduler = TestScheduler()
    private val observer = upstream.debounce(100L, scheduler).test()

    @Test
    fun does_not_emit_WHEN_timeout_not_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(99)

        observer.assertNoValues()
    }

    @Test
    fun emits_WHEN_timeout_is_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(100L)

        observer.assertValue(1)
    }

    @Test
    fun does_not_emit_WHEN_timeout_since_last_item_not_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(50L)
        upstream.onNext(2)
        scheduler.timer.advanceBy(99L)

        observer.assertNoValues()
    }

    @Test
    fun emits_WHEN_timeout_since_last_item_is_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(50L)
        upstream.onNext(2)
        scheduler.timer.advanceBy(100L)

        observer.assertValue(2)
    }

    @Test
    fun emits_last_unprocessed_item_WHEN_completed() {
        upstream.onNext(1)
        upstream.onNext(2)
        upstream.onComplete()

        observer.assertValue(2)
    }

    @Test
    fun does_not_emit_last_item_WHEN_already_emitted_and_completed() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(100L)
        observer.reset()
        upstream.onComplete()

        observer.assertNoValues()
    }
}