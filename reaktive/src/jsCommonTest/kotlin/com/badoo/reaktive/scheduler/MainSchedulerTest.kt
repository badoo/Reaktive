package com.badoo.reaktive.scheduler

import com.badoo.reaktive.observable.flatMapSingle
import com.badoo.reaktive.observable.observableOf
import com.badoo.reaktive.observable.toList
import com.badoo.reaktive.single.doOnBeforeFinally
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.singleTimer
import com.badoo.reaktive.test.single.AsyncTestResult
import com.badoo.reaktive.test.single.testAwait
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class MainSchedulerTest {
    @Test
    fun submits_task_to_executor() {
        val scheduler = MainScheduler()
        val executor = scheduler.newExecutor()
        executor.submit {}
    }

    @Test
    fun delayed_task_executed_with_delay(): AsyncTestResult {
        val scheduler = MainScheduler()
        val executor = scheduler.newExecutor()
        var executed = false
        executor.submit(200.milliseconds) { executed = true }

        return observableOf(100.milliseconds, 300.milliseconds)
            .flatMapSingle { timeout ->
                singleTimer(timeout, scheduler = scheduler)
                    .map { executed }
            }
            .toList()
            .testAwait { (before, after) ->
                assertFalse(before)
                assertTrue(after)
            }
    }

    @Test
    fun interval_task_executed_at_interval(): AsyncTestResult {
        val scheduler = MainScheduler()
        val executor = scheduler.newExecutor()
        var counter = 0
        val items = mutableListOf<Int>()

        executor.submit(period = 256.milliseconds) {
            items.add(counter++)
        }

        val checkTicks =
            observableOf(128.milliseconds, 384.milliseconds, 640.milliseconds, 896.milliseconds, 1152.milliseconds)
                .flatMapSingle { timeout ->
                    singleTimer(timeout, scheduler = scheduler)
                        .map { items.toList() }
                }

        val expectedResults =
            listOf(
                emptyList(),
                listOf(0),
                listOf(0, 1),
                listOf(0, 1, 2),
                listOf(0, 1, 2, 3),
            )

        return checkTicks
            .toList()
            // Required to pass test on NodeJS environment since runtime waits
            // for all tasks to cancel or finish their work.
            .doOnBeforeFinally(executor::cancel)
            .testAwait { results ->
                assertEquals(expectedResults, results)
            }
    }
}
