package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.exceptions.TimeoutException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimeoutTest : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ timeout(1000L, TestScheduler()) }) {

    private val upstream = TestMaybe<Int?>()
    private val other = TestMaybe<Int?>()
    private val scheduler = TestScheduler()

    @Test
    fun does_not_produce_error_WHEN_timeout_reached_while_succeeding() {
        val errorRef = AtomicReference<Throwable?>(null)

        upstream
            .timeout(1000L, scheduler)
            .subscribe(
                object : MaybeObserver<Int?> {
                    override fun onSubscribe(disposable: Disposable) {
                    }

                    override fun onSuccess(value: Int?) {
                        scheduler.timer.advanceBy(1000L)
                    }

                    override fun onComplete() {
                    }

                    override fun onError(error: Throwable) {
                        errorRef.value = error
                    }
                }
            )

        upstream.onSuccess(0)

        assertNull(errorRef.value)
    }

    @Test
    fun succeeds_WHEN_other_succeeds() {
        val observer = upstream.timeout(1000L, scheduler, other).test()

        scheduler.timer.advanceBy(1000L)
        observer.reset()
        other.onSuccess(1)

        observer.assertSuccess(1)
    }

    @Test
    fun completes_WHEN_other_completed() {
        val observer = upstream.timeout(1000L, scheduler, other).test()

        scheduler.timer.advanceBy(1000L)
        other.onComplete()

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_other_produced_error() {
        val observer = upstream.timeout(1000L, scheduler, other).test()
        val error = Exception()

        scheduler.timer.advanceBy(1000L)
        other.onError(error)

        observer.assertError(error)
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
