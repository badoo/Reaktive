package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.clock.DefaultClock
import com.badoo.reaktive.utils.lock.ConditionLock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.lock.waitForOrFail
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class SchedulerCoroutineDispatcherJvmTest {

    @Test
    fun executes_with_delay() {
        val scheduler = TestScheduler(isManualProcessing = false)
        val dispatcher = SchedulerCoroutineDispatcher(scheduler = scheduler)
        val startTime = DefaultClock.uptime
        var endTime = Duration.ZERO
        val lock = ConditionLock()

        GlobalScope.launch(dispatcher) {
            delay(500.milliseconds)
            lock.synchronized {
                endTime = DefaultClock.uptime
                lock.signal()
            }
        }

        lock.synchronized {
            lock.waitForOrFail { endTime.isPositive() }
        }

        assertTrue(endTime - startTime >= 500.milliseconds)
    }
}
