package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.coroutinesinterop.test.waitForOrFail
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.clock.DefaultClock
import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.synchronized
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertTrue

class SchedulerCoroutineDispatcherJvmTest {

    @Test
    fun executes_with_delay() {
        val scheduler = TestScheduler(isManualProcessing = false)
        val dispatcher = SchedulerCoroutineDispatcher(scheduler = scheduler)
        val startTimeMillis = DefaultClock.uptimeMillis
        val endTimeMillis = AtomicLong()
        val lock = Lock()
        val condition = lock.newCondition()

        GlobalScope.launch(dispatcher) {
            delay(500L)
            lock.synchronized {
                endTimeMillis.value = DefaultClock.uptimeMillis
                condition.signal()
            }
        }

        lock.synchronized {
            condition.waitForOrFail {
                endTimeMillis.value > 0L
            }
        }

        assertTrue(endTimeMillis.value - startTimeMillis >= 500L)
    }
}
