package com.badoo.reaktive.test.scheduler

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.test.assert.assertTrue

fun TestScheduler.assertAllExecutorsDisposed(): TestScheduler {
    assertTrue(executors.all(Scheduler.Executor::isDisposed), "Not all executors are disposed")

    return this
}
