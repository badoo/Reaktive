package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.lock.ConditionLock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.lock.waitForOrFail
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

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
        var isFinished = false
        val lock = ConditionLock()

        fun executeTask() {
            if (counter.addAndGet(1) == TASK_TOTAL_COUNT) {
                lock.synchronized {
                    isFinished = true
                    lock.signal()
                }
            }
        }

        repeat(TASK_PER_EXECUTOR_COUNT) {
            executors.forEach {
                it.submit(task = ::executeTask)
            }
        }

        lock.synchronized {
            lock.waitForOrFail(20.seconds) { isFinished }
        }
    }

    private companion object {
        private const val EXECUTOR_COUNT = 10
        private const val TASK_PER_EXECUTOR_COUNT = 100
        private const val TASK_TOTAL_COUNT = EXECUTOR_COUNT * TASK_PER_EXECUTOR_COUNT
    }
}
