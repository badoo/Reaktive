package com.badoo.reaktive.observable

import com.badoo.reaktive.base.exceptions.TimeoutException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimeoutTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ timeout(1000L, TestScheduler()) }) {

    private val upstream = TestObservable<Int?>()
    private val other = TestObservable<Int?>()
    private val scheduler = TestScheduler()

    @Test
    fun emits_all_values_in_the_same_order() {
        val observer = upstream.timeout(1000L, scheduler).test()

        upstream.onNext(0, null, 1, null, 2)

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun does_not_produce_error_WHEN_timeout_reached_while_emitting_value() {
        val errorRef = AtomicReference<Throwable?>(null)

        upstream
            .timeout(1000L, scheduler)
            .subscribe(
                object : ObservableObserver<Int?> {
                    override fun onSubscribe(disposable: Disposable) {
                    }

                    override fun onNext(value: Int?) {
                        scheduler.timer.advanceBy(1000L)
                    }

                    override fun onComplete() {
                    }

                    override fun onError(error: Throwable) {
                        errorRef.value = error
                    }
                }
            )

        upstream.onNext(0)
        upstream.onNext(1)

        assertNull(errorRef.value)
    }

    @Test
    fun does_not_produce_error_WHEN_timeout_not_reached_after_first_value() {
        val observer = upstream.timeout(1000L, scheduler).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(999L)

        observer.assertNotError()
    }

    @Test
    fun produces_error_WHEN_timeout_reached_after_first_value() {
        val observer = upstream.timeout(1000L, scheduler).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(1000L)

        observer.assertError { it is TimeoutException }
    }

    @Test
    fun does_not_produce_error_WHEN_timeout_not_reached_after_second_value() {
        val observer = upstream.timeout(1000L, scheduler).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(999L)
        upstream.onNext(1)
        scheduler.timer.advanceBy(999L)

        observer.assertNotError()
    }

    @Test
    fun produces_error_WHEN_timeout_reached_after_second_value() {
        val observer = upstream.timeout(1000L, scheduler).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(999L)
        upstream.onNext(1)
        scheduler.timer.advanceBy(1000L)

        observer.assertError { it is TimeoutException }
    }

    @Test
    fun does_not_subscribe_to_other_WHEN_timeout_not_reached_after_first_value() {
        upstream.timeout(1000L, scheduler, other).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(999L)

        assertFalse(other.hasSubscribers)
    }

    @Test
    fun subscribes_to_other_WHEN_timeout_reached_after_first_value() {
        upstream.timeout(1000L, scheduler, other).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(1000L)

        assertTrue(other.hasSubscribers)
    }

    @Test
    fun does_not_produce_error_WHEN_timeout_reached_after_first_value_and_has_other() {
        val observer = upstream.timeout(1000L, scheduler, other).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(1000L)

        observer.assertNotError()
    }

    @Test
    fun does_not_subscribe_to_other_WHEN_timeout_not_reached_after_second_value() {
        upstream.timeout(1000L, scheduler, other).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(999L)
        upstream.onNext(1)
        scheduler.timer.advanceBy(999L)

        assertFalse(other.hasSubscribers)
    }

    @Test
    fun subscribes_to_other_WHEN_timeout_reached_after_second_value() {
        upstream.timeout(1000L, scheduler, other).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(999L)
        upstream.onNext(1)
        scheduler.timer.advanceBy(1000L)

        assertTrue(other.hasSubscribers)
    }

    @Test
    fun does_not_produce_error_WHEN_timeout_reached_after_second_value_and_has_other() {
        val observer = upstream.timeout(1000L, scheduler, other).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(999L)
        upstream.onNext(1)
        scheduler.timer.advanceBy(1000L)

        observer.assertNotError()
    }

    @Test
    fun emits_all_values_in_correct_order_from_other() {
        val observer = upstream.timeout(1000L, scheduler, other).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(1000L)
        observer.reset()
        other.onNext(1, null, 2, null, 3)

        observer.assertValues(1, null, 2, null, 3)
    }

    @Test
    fun completes_WHEN_other_completed() {
        val observer = upstream.timeout(1000L, scheduler, other).test()

        upstream.onNext(0)
        scheduler.timer.advanceBy(1000L)
        other.onComplete()

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_other_produced_error() {
        val observer = upstream.timeout(1000L, scheduler, other).test()
        val error = Exception()

        upstream.onNext(0)
        scheduler.timer.advanceBy(1000L)
        other.onError(error)

        observer.assertError(error)
    }

    @Test
    fun does_not_subscribe_to_other_second_time_WHEN_timeout_reached_after_second_value_and_has_other() {
        val upstreamObserver = AtomicReference<ObservableObserver<Int>?>(null)
        val upstream =
            observableUnsafe<Int> {
                upstreamObserver.value = it
                it.onSubscribe(Disposable())
            }

        val otherSubscribeCount = AtomicInt()
        val other = observable<Int> { otherSubscribeCount.addAndGet(1) }

        upstream.timeout(1000L, scheduler, other).test()

        upstreamObserver.value!!.onNext(0)
        scheduler.timer.advanceBy(1000L)
        upstreamObserver.value!!.onNext(1)
        scheduler.timer.advanceBy(1000L)

        assertEquals(1, otherSubscribeCount.value)
    }

    @Test
    fun does_not_produce_error_WHEN_timeout_not_reached_after_subscribe() {
        val observer = upstream.timeout(1000L, scheduler).test()

        scheduler.timer.advanceBy(999L)

        observer.assertNotError()
    }

    @Test
    fun produces_error_WHEN_timeout_reached_after_subscribe() {
        val observer = upstream.timeout(1000L, scheduler).test()

        scheduler.timer.advanceBy(1000L)

        observer.assertError { it is TimeoutException }
    }

    @Test
    fun does_not_subscribe_to_other_WHEN_timeout_not_reached_after_subscribe() {
        upstream.timeout(1000L, scheduler, other).test()

        scheduler.timer.advanceBy(999L)

        assertFalse(other.hasSubscribers)
    }

    @Test
    fun subscribes_to_other_WHEN_timeout_reached_after_subscribe() {
        upstream.timeout(1000L, scheduler, other).test()

        scheduler.timer.advanceBy(1000L)

        assertTrue(other.hasSubscribers)
    }

    @Test
    fun does_not_produce_error_WHEN_timeout_reached_after_subscribe_and_has_other() {
        val observer = upstream.timeout(1000L, scheduler, other).test()

        scheduler.timer.advanceBy(1000L)

        observer.assertNotError()
    }
}
