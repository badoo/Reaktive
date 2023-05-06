package com.badoo.reaktive.utils

import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.system.getTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class DelayQueueTest {

    private val queue = DelayQueue<Int>()

    @Test
    fun provides_item_with_delay() {
        val startMillis = getTimeMillis()

        queue.offer(0, 200.milliseconds)
        val value = queue.take()

        assertTrue(getTimeMillis() - startMillis >= 200L)
        assertEquals(0, value)
    }

    @Test
    fun provides_items_in_correct_order_and_with_correct_delays() {
        val startMillis = getTimeMillis()

        queue.offer(2, 300.milliseconds)
        queue.offer(3, 500.milliseconds)
        queue.offer(1, 200.milliseconds)
        val v1 = queue.take()
        val t1 = getTimeMillis() - startMillis
        val v2 = queue.take()
        val t2 = getTimeMillis() - startMillis
        val v3 = queue.take()
        val t3 = getTimeMillis() - startMillis

        assertTrue(t1 >= 200L)
        assertTrue(t2 >= 300L)
        assertTrue(t3 >= 500L)
        assertEquals(1, v1)
        assertEquals(2, v2)
        assertEquals(3, v3)
    }

    @Test
    fun removes_first_item() {
        val startMillis = getTimeMillis()

        queue.offer(0, 200.milliseconds)
        queue.offer(1, 100.milliseconds)
        queue.removeFirst()
        val value = queue.take()

        assertTrue(getTimeMillis() - startMillis >= 200L)
        assertEquals(0, value)
    }

    @Test
    fun able_to_offer_and_take_from_different_threads() {
        Worker
            .start(true)
            .execute(TransferMode.SAFE, { queue }) { it.offer(0, Duration.ZERO) }

        val value = queue.take()

        assertEquals(0, value)
    }

    @Test
    fun provide_items_with_same_delay_in_the_same_order() {
        queue.offer(0, Duration.ZERO)
        queue.offer(1, Duration.ZERO)
        queue.offer(2, Duration.ZERO)
        val values = listOf(queue.take(), queue.take(), queue.take())

        assertEquals(listOf(0, 1, 2), values)
    }

    @Test
    fun take_returns_null_when_terminated() {
        queue.offer(0, Duration.ZERO)
        queue.terminate()
        val value = queue.take()

        assertNull(value)
    }

    @Test
    fun removeIf_removes_items() {
        queue.offer(value = 0, timeout = Duration.ZERO)
        queue.offer(value = 1, timeout = Duration.ZERO)
        queue.offer(value = 2, timeout = Duration.ZERO)
        queue.offer(value = 3, timeout = Duration.ZERO)

        queue.removeIf { (it == 1) || (it == 3) }

        val values = listOf(queue.take(), queue.take())

        assertEquals(listOf(0, 2), values)
    }
}
