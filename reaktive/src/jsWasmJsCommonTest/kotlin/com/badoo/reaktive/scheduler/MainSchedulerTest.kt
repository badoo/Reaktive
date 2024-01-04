package com.badoo.reaktive.scheduler

import kotlin.test.Test

class MainSchedulerTest {
    @Test
    fun submits_task_to_executor() {
        val scheduler = MainScheduler()
        val executor = scheduler.newExecutor()
        executor.submit {}
    }
}
