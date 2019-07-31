package com.badoo.reaktive.utils

import kotlin.system.getTimeNanos
import kotlin.test.Test
import kotlin.test.assertTrue

class ConditionTest {

    @Test
    fun awaits_for_specified_time_before_give_up() {
        val lock = Lock()
        val condition = lock.newCondition()
        val startNanos = getTimeNanos()
        val timeoutNanos = 300000000L // 300 ms

        lock.synchronized {
            condition.await(timeoutNanos)
        }

        assertTrue(getTimeNanos() - startNanos >= timeoutNanos)
    }
}