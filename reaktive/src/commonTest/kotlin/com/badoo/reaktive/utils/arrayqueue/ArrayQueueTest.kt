package com.badoo.reaktive.utils.arrayqueue

import com.badoo.reaktive.utils.queue.ArrayQueue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ArrayQueueTest {

    private val queue = ArrayQueue<String?>()

    @Test
    fun initial_size_is_zero() {
        assertEquals(0, queue.size)
    }

    @Test
    fun size_returns_1_AFTER_offer_one_item() {
        offer(1)

        assertEquals(1, queue.size)
    }

    @Test
    fun size_returns_0_AFTER_offer_one_item_and_poll_one_item() {
        offer(1)
        poll(1)

        assertEquals(0, queue.size)
    }

    @Test
    fun size_returns_100_AFTER_offer_100_items() {
        offer(100)

        assertEquals(100, queue.size)
    }

    @Test
    fun poll_returns_same_value_AFTER_offer() {
        queue.offer("a")

        assertEquals("a", queue.poll())
    }

    @Test
    fun all_peek_calls_return_same_value_AFTER_offer() {
        queue.offer("a")

        repeat(50) {
            assertEquals("a", queue.peek)
        }
    }

    @Test
    fun peek_returns_null_AFTER_offer_one_item_and_poll_one_item() {
        offer(1)
        poll(1)

        assertNull(queue.peek)
    }

    @Test
    fun second_and_other_polls_return_null_AFTER_one_offer_and_one_poll() {
        offer(1)
        poll(1)

        repeat(50) {
            assertNull(queue.poll())
        }
    }

    @Test
    fun poll_returns_second_item_AFTER_offer_first_item_and_poll_twice_and_offer_second_item() {
        offer(1)
        poll(2)
        queue.offer("b")

        assertEquals("b", queue.poll())
    }

    @Test
    fun peek_returns_second_item_AFTER_offer_first_item_and_poll_twice_and_offer_second_item() {
        offer(1)
        poll(2)
        queue.offer("b")

        assertEquals("b", queue.peek)
    }

    @Test
    fun poll_100_times_return_same_items_AFTER_offer_100_items() {
        val src = List(100, Int::toString)

        offer(src)

        assertEquals(src, poll(100))
    }

    @Test
    fun poll_100_times_return_whole_src_items_AFTER_offer_50_dummy_items_and_offer_first_50_src_items_and_poll_50_dummy_values_and_offer_last_50_src_items() {
        val src = List(100, Int::toString)

        offer(50)
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 100) {
            queue.offer(src[i])
        }

        assertEquals(src, poll(100))
    }

    @Test
    fun size_returns_1_AFTER_offer_100_times_and_poll_50_times_and_offer_50_times_and_poll_99_times() {
        offer(100)
        poll(50)
        offer(50)
        poll(99)

        assertEquals(1, queue.size)
    }

    @Test
    fun size_returns_0_AFTER_offer_100_times_and_poll_50_times_and_offer_50_times_and_poll_100_times() {
        offer(100)
        poll(50)
        offer(50)
        poll(100)

        assertEquals(0, queue.size)
    }

    @Test
    fun poll_150_times_return_whole_src_items_AFTER_offer_50_dummy_items_and_offer_first_50_src_items_and_poll_50_dummy_values_and_offer_last_100_src_items() {
        val src = List(150, Int::toString)

        offer(50)
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 150) {
            queue.offer(src[i])
        }

        assertEquals(src, poll(150))
    }

    @Test
    fun size_returns_1_AFTER_offer_100_times_and_poll_50_times_and_offer_100_times_and_poll_149_times() {
        offer(100)
        poll(50)
        offer(100)
        poll(149)

        assertEquals(1, queue.size)
    }

    @Test
    fun size_returns_0_AFTER_offer_100_times_and_poll_50_times_and_offer_100_times_and_poll_150_times() {
        offer(100)
        poll(50)
        offer(100)
        poll(150)

        assertEquals(0, queue.size)
    }

    @Test
    fun size_returns_0_AFTER_clear_non_empty_queue() {
        offer(100)
        queue.clear()

        assertEquals(0, queue.size)
    }

    @Test
    fun size_returns_1_AFTER_clear_non_empty_queue_and_offer_one_item() {
        offer(100)
        queue.clear()
        offer(1)

        assertEquals(1, queue.size)
    }

    @Test
    fun poll_returns_null_AFTER_clear_non_empty_queue() {
        offer(100)
        queue.clear()

        assertNull(queue.poll())
    }

    @Test
    fun poll_returns_last_item_AFTER_clear_non_empty_queue_and_offer_one_item() {
        offer(100)
        queue.clear()
        queue.offer("a")

        assertEquals("a", queue.poll())
    }

    @Test
    fun size_returns_0_AFTER_clear_non_empty_queue_and_offer_one_item_and_poll_one_item() {
        offer(100)
        queue.clear()
        offer(1)
        poll(1)

        assertEquals(0, queue.size)
    }

    @Test
    fun poll_returns_null_AFTER_clear_non_empty_queue_and_offer_one_item_and_poll_one_item() {
        offer(100)
        queue.clear()
        offer(1)
        poll(1)

        assertNull(queue.poll())
    }

    @Test
    fun size_returns_1_AFTER_initial_capacity_minus_1_offers_and_same_polls_and_one_additional_offer() {
        val count = ArrayQueue.INITIAL_CAPACITY - 1
        offer(count)
        poll(count)
        offer(1)
        assertEquals(1, queue.size)
    }

    @Test
    fun size_returns_initial_capacity_AFTER_same_offers() {
        offer(ArrayQueue.INITIAL_CAPACITY)
        assertEquals(ArrayQueue.INITIAL_CAPACITY, queue.size)
    }

    @Test
    fun size_returns_initial_capacity_plus_1_AFTER_same_offers() {
        offer(ArrayQueue.INITIAL_CAPACITY + 1)
        assertEquals(ArrayQueue.INITIAL_CAPACITY + 1, queue.size)
    }

    @Test
    fun size_returns_initial_capacity_AFTER_initial_capacity_offers_and_2_polls_and_2_offers() {
        offer(List(ArrayQueue.INITIAL_CAPACITY, Int::toString))
        poll(2)
        offer(List(2, Int::toString))
        assertEquals(ArrayQueue.INITIAL_CAPACITY, queue.size)
    }

    private fun offer(iterable: Iterable<String?>) {
        iterable.forEach(queue::offer)
    }

    private fun offer(count: Int) {
        for (i in 0 until count) {
            queue.offer("dummy_$i")
        }
    }

    private fun poll(count: Int): List<String?> = List(count) { queue.poll() }
}