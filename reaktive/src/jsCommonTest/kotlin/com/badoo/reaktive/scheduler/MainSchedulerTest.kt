package com.badoo.reaktive.scheduler

import com.badoo.reaktive.observable.flatMapSingle
import com.badoo.reaktive.observable.observableOf
import com.badoo.reaktive.observable.toList
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

        return observableOf(
            100.milliseconds, 300.milliseconds
        )
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
        var counter = 0L
        val items = mutableListOf<Long>()

        val delayBetweenTasks = 50.milliseconds

        executor.submit(period = delayBetweenTasks) {
            items.add(counter++)
        }

        val amountToTest = 10
        val testTickShift = 25L

        val testTicks = 1..amountToTest
        val expectedItems = testTicks.map { tick ->
            (0 until tick).map { it.toLong() }.toList()
        }.toList()

        return observableOf(
            *testTicks
                .map { it * 50L + testTickShift }
                .map { it.milliseconds }
                .toTypedArray()
        )
            .flatMapSingle { timeout ->
                singleTimer(timeout, scheduler = scheduler)
                    .map { items.toList() }
            }
            .toList()
            .testAwait { tickStates ->
                assertEquals(expectedItems, tickStates)
            }
    }
}
