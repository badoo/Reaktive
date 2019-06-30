package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test

interface DelayErrorTests {

    @Test
    fun does_not_produce_error_synchronously_with_delayError_false_WHEN_upstream_produced_error()

    @Test
    fun produces_error_without_delay_with_delayError_false_WHEN_upstream_produced_error()

    @Test
    fun does_not_produce_error_synchronously_with_delayError_true_WHEN_upstream_produced_error()

    @Test
    fun does_not_produce_error_with_delayError_true_WHEN_upstream_produced_error_and_timeout_not_reached()

    @Test
    fun produces_error_with_delayError_is_true_WHEN_upstream_produced_error_and_timeout_reached()

    companion object {
        operator fun <S : ErrorCallback> invoke(
            upstream: S,
            delay: S.(delayMillis: Long, scheduler: Scheduler, delayError: Boolean) -> TestObserver
        ): DelayErrorTests =
            object : DelayErrorTests {
                override fun does_not_produce_error_synchronously_with_delayError_false_WHEN_upstream_produced_error() {
                    val scheduler = TestScheduler(isManualProcessing = true)
                    val observer = upstream.delay(1000L, scheduler, false)

                    upstream.onError(Exception())

                    observer.assertNotError()
                }

                override fun produces_error_without_delay_with_delayError_false_WHEN_upstream_produced_error() {
                    val scheduler = TestScheduler()
                    val observer = upstream.delay(1000L, scheduler, false)
                    val error = Exception()

                    upstream.onError(error)

                    observer.assertError(error)
                }

                override fun does_not_produce_error_synchronously_with_delayError_true_WHEN_upstream_produced_error() {
                    val scheduler = TestScheduler(isManualProcessing = true)
                    val observer = upstream.delay(1000L, scheduler, true)

                    upstream.onError(Exception())

                    observer.assertNotError()
                }

                override fun does_not_produce_error_with_delayError_true_WHEN_upstream_produced_error_and_timeout_not_reached() {
                    val scheduler = TestScheduler()
                    val observer = upstream.delay(1000L, scheduler, true)

                    upstream.onError(Exception())
                    scheduler.timer.advanceBy(999L)

                    observer.assertNotError()
                }

                override fun produces_error_with_delayError_is_true_WHEN_upstream_produced_error_and_timeout_reached() {
                    val scheduler = TestScheduler()
                    val observer = upstream.delay(1000L, scheduler, true)
                    val error = Exception()

                    upstream.onError(error)
                    scheduler.timer.advanceBy(1000L)

                    observer.assertError(error)
                }
            }
    }
}