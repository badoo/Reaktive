package com.badoo.reaktive.observable

import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test

class BufferSimpleTests {

    private val upstream = TestObservable<Int>()

    @Test
    fun buffer_spanMillis_emits_correct_values() {
        val scheduler = TestScheduler()
        val timer = scheduler.timer
        val observer = upstream.buffer(spanMillis = 1000L, scheduler = scheduler, limit = 3).test()

        upstream.onNext(1, 2)
        timer.advanceBy(999L)
        upstream.onNext(3)
        timer.advanceBy(1L)
        upstream.onNext(4, 5)
        timer.advanceBy(1000L)
        upstream.onNext(6, 7, 8)
        timer.advanceBy(1000L)
        upstream.onNext(9, 10, 11, 12)
        timer.advanceBy(1000L)
        upstream.onNext(13, 14, 15)
        timer.advanceBy(1000L)
        upstream.onNext(16, 17)
        upstream.onComplete()

        observer.assertValues(
            listOf(1, 2, 3),
            listOf(4, 5),
            listOf(6, 7, 8),
            listOf(9, 10, 11),
            listOf(13, 14, 15),
            listOf(16, 17),
        )
    }

    @Test
    fun buffer_boundaries_emits_correct_values() {
        val boundaries = TestObservable<Unit>()
        val observer = upstream.buffer(boundaries = boundaries, limit = 3).test()

        upstream.onNext(1, 2, 3)
        boundaries.onNext(Unit)
        upstream.onNext(4, 5)
        boundaries.onNext(Unit)
        upstream.onNext(6, 7, 8)
        boundaries.onNext(Unit)
        upstream.onNext(9, 10, 11, 12)
        boundaries.onNext(Unit)
        upstream.onNext(13, 14, 15)
        boundaries.onNext(Unit)
        upstream.onNext(16, 17)
        upstream.onComplete()

        observer.assertValues(
            listOf(1, 2, 3),
            listOf(4, 5),
            listOf(6, 7, 8),
            listOf(9, 10, 11),
            listOf(13, 14, 15),
            listOf(16, 17),
        )
    }

    @Test
    fun buffer_spanMillis_skipMillis_emits_correct_values() {
        val scheduler = TestScheduler()
        val timer = scheduler.timer
        val observer = upstream.buffer(spanMillis = 1000L, skipMillis = 600L, scheduler = scheduler, limit = 13).test()

        upstream.onNext(1, 2)
        timer.advanceBy(599L)
        upstream.onNext(3)
        timer.advanceBy(1L)
        upstream.onNext(4, 5)
        timer.advanceBy(399L)
        upstream.onNext(6)
        timer.advanceBy(1L)
        upstream.onNext(7, 8, 9, 10)
        timer.advanceBy(200L)
        upstream.onNext(11, 12, 13)
        timer.advanceBy(200L)
        upstream.onNext(14, 15)
        timer.advanceBy(200L)
        upstream.onNext(16, 17)
        timer.advanceBy(200L)
        upstream.onNext(18, 19, 20, 21, 22, 23, 24)
        upstream.onComplete()

        observer.assertValues(
            listOf(1, 2, 3, 4, 5, 6),
            listOf(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
            listOf(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23),
            listOf(18, 19, 20, 21, 22, 23, 24),
        )
    }

    @Test
    fun buffer_opening_closing_emits_correct_values() {
        val opening = TestObservable<Int>()
        val closing = List(5) { TestCompletable() }
        val observer = upstream.buffer(opening = opening, closing = { closing[it] }, limit = 5).test()

        upstream.onNext(1)
        opening.onNext(0)
        upstream.onNext(2)
        opening.onNext(1)
        upstream.onNext(3)
        opening.onNext(2)
        upstream.onNext(4)
        closing[0].onComplete()
        upstream.onNext(5)
        closing[2].onComplete()
        upstream.onNext(6)
        opening.onNext(3)
        upstream.onNext(7)
        upstream.onNext(8)
        upstream.onNext(9)
        upstream.onComplete()

        observer.assertValues(
            listOf(2, 3, 4),
            listOf(4, 5),
            listOf(3, 4, 5, 6, 7),
            listOf(7, 8, 9),
        )
    }
}
