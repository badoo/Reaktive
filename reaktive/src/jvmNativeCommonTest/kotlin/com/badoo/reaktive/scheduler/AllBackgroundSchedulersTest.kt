package com.badoo.reaktive.scheduler

import com.badoo.reaktive.test.waitForOrFail
import com.badoo.reaktive.utils.NANOS_IN_SECOND
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue
import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.synchronized
import kotlin.test.Test

class AllBackgroundSchedulersTest {

    @Test
    fun computationScheduler() {
        testScheduler(computationScheduler)
    }

    @Test
    fun ioScheduler() {
        testScheduler(ioScheduler)
    }

    @Test
    fun singleScheduler() {
        testScheduler(singleScheduler)
    }

    @Test
    fun newThreadScheduler() {
        testScheduler(newThreadScheduler)
    }

    private fun testScheduler(scheduler: Scheduler) {
        val executors = List(EXECUTOR_COUNT) { scheduler.newExecutor() }

        val counter = AtomicInt()
        var isFinished by AtomicBoolean()
        val lock = Lock()
        val condition = lock.newCondition()

        fun executeTask() {
            if (counter.addAndGet(1) == TASK_TOTAL_COUNT) {
                lock.synchronized {
                    isFinished = true
                    condition.signal()
                }
            }
        }

        repeat(TASK_PER_EXECUTOR_COUNT) {
            executors.forEach {
                it.submit(task = ::executeTask)
            }
        }

        lock.synchronized {
            condition.waitForOrFail(20L * NANOS_IN_SECOND) { isFinished }
        }
    }

    private companion object {
        private const val EXECUTOR_COUNT = 10
        private const val TASK_PER_EXECUTOR_COUNT = 100
        private const val TASK_TOTAL_COUNT = EXECUTOR_COUNT * TASK_PER_EXECUTOR_COUNT
    }
}
