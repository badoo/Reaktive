package com.badoo.reaktive.utils.queue

import com.badoo.reaktive.utils.queue.ArrayQueue.Companion.INITIAL_CAPACITY
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal interface QueueTests {

    @Test
    fun initial_size_is_zero()

    @Test
    fun empty_after_creation()

    @Test
    fun not_empty_WHEN_offered_one_item()

    @Test
    fun empty_WHEN_offered_and_polled_one_item()

    @Test
    fun not_empty_WHEN_offered_100_items()

    @Test
    fun empty_WHEN_offered_and_polled_100_items()

    @Test
    fun not_empty_WHEN_offered_100_items_and_polled_99_items()

    @Test
    fun not_empty_WHEN_offered_INTIAL_CAPACITY_items()

    @Test
    fun size_is_1_WHEN_offer_one_item()

    @Test
    fun size_is_0_WHEN_offer_and_poll()

    @Test
    fun size_is_100_WHEN_offer_one_100_items()

    @Test
    fun size_is_INITIAL_CAPACITY_WHEN_offered_INITIAL_CAPACITY_items()

    @Test
    fun offer_poll_returns_value()

    @Test
    fun offer_all_peeks_return_same_value()

    @Test
    fun offer_poll_peek_returns_null()

    @Test
    fun offer_second_and_other_polls_returnNull()

    @Test
    fun offer_poll_poll_offer_poll_returns_last_item()

    @Test
    fun offer_poll_poll_offer_peek_returns_last_item()

    @Test
    fun a_100_offers_100_polls_return_valid_items()

    @Test
    fun a_100_offers_50_polls_50_offers_100_polls_return_valid_items()

    @Test
    fun size_is_1_WHEN_100_offers_50_polls_50_offers_99_polls()

    @Test
    fun size_is_0_WHEN_100_offers_50_polls_50_offers_100_polls()

    @Test
    fun a_100_offers_50_polls_100_offers_150_polls_return_valid_items()

    @Test
    fun size_is_1_WHEN_100_offers_50_polls_100_offers_149_polls()

    @Test
    fun size_is_0_WHEN_100_offers_50_polls_100_offers_150_polls()

    @Test
    fun size_is_0_after_clear()

    @Test
    fun size_is_1_after_clear_and_1_offer()

    @Test
    fun poll_returns_Null_after_clear()

    @Test
    fun poll_returns_correct_item_after_clear_and_one_offer()

    @Test
    fun size_is_0_after_clear_one_offer_and_one_poll()

    @Test
    fun poll_returns_Null_after_clear_one_offer_and_one_poll()

    @Test
    fun size_is_one_after_initial_capacity_Minus_1_offers_and_same_polls_and_one_additional_offer()

    @Test
    fun size_is_initial_capacity_after_same_offers()

    @Test
    fun size_is_initial_capacity_plus_1_after_same_offers()

    @Test
    fun size_is_initial_capacity_after_initial_capacity_offers_and_two_polls_and_two_offers()
}

@Ignore
internal class QueueTestsImpl(
    private val queue: Queue<String?>
) : QueueTests {

    override fun initial_size_is_zero() {
        assertEquals(0, queue.size)
    }

    override fun empty_after_creation() {
        assertTrue(queue.isEmpty)
    }

    override fun not_empty_WHEN_offered_one_item() {
        queue.offer("a")

        assertFalse(queue.isEmpty)
    }

    override fun empty_WHEN_offered_and_polled_one_item() {
        queue.offer("a")
        queue.poll()

        assertTrue(queue.isEmpty)
    }

    override fun not_empty_WHEN_offered_100_items() {
        repeat(100) {
            queue.offer("a")
        }

        assertFalse(queue.isEmpty)
    }

    override fun empty_WHEN_offered_and_polled_100_items() {
        offer(100)
        poll(100)

        assertTrue(queue.isEmpty)
    }

    override fun not_empty_WHEN_offered_100_items_and_polled_99_items() {
        offer(100)
        poll(99)

        assertFalse(queue.isEmpty)
    }

    override fun not_empty_WHEN_offered_INTIAL_CAPACITY_items() {
        offer(INITIAL_CAPACITY)

        assertFalse(queue.isEmpty)
    }

    override fun size_is_1_WHEN_offer_one_item() {
        queue.offer("a")
        assertEquals(1, queue.size)
    }

    override fun size_is_0_WHEN_offer_and_poll() {
        queue.offer("a")
        queue.poll()
        assertEquals(0, queue.size)
    }

    override fun size_is_100_WHEN_offer_one_100_items() {
        offer(100)
        assertEquals(100, queue.size)
    }

    override fun size_is_INITIAL_CAPACITY_WHEN_offered_INITIAL_CAPACITY_items() {
        offer(INITIAL_CAPACITY)

        assertEquals(INITIAL_CAPACITY, queue.size)
    }

    override fun offer_poll_returns_value() {
        queue.offer("a")
        assertEquals("a", queue.poll())
    }

    override fun offer_all_peeks_return_same_value() {
        queue.offer("a")
        repeat(50) {
            assertEquals("a", queue.peek)
        }
    }

    override fun offer_poll_peek_returns_null() {
        queue.offer("a")
        queue.poll()
        assertNull(queue.peek)
    }

    override fun offer_second_and_other_polls_returnNull() {
        queue.offer("a")
        queue.poll()
        repeat(50) {
            assertNull(queue.poll())
        }
    }

    override fun offer_poll_poll_offer_poll_returns_last_item() {
        queue.offer("a")
        poll(2)
        queue.offer("b")
        assertEquals("b", queue.poll())
    }

    override fun offer_poll_poll_offer_peek_returns_last_item() {
        queue.offer("a")
        poll(2)
        queue.offer("b")
        assertEquals("b", queue.peek)
    }

    override fun a_100_offers_100_polls_return_valid_items() {
        val src = List(100, Int::toString)
        offer(src)
        assertEquals(src, poll(100))
    }

    override fun a_100_offers_50_polls_50_offers_100_polls_return_valid_items() {
        val src = items(100)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 100) {
            queue.offer(src[i])
        }
        assertEquals(src, poll(100))
    }

    override fun size_is_1_WHEN_100_offers_50_polls_50_offers_99_polls() {
        val src = items(100)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 100) {
            queue.offer(src[i])
        }
        poll(99)
        assertEquals(1, queue.size)
    }

    override fun size_is_0_WHEN_100_offers_50_polls_50_offers_100_polls() {
        val src = items(100)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 100) {
            queue.offer(src[i])
        }
        poll(100)
        assertEquals(0, queue.size)
    }

    override fun a_100_offers_50_polls_100_offers_150_polls_return_valid_items() {
        val src = items(150)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 150) {
            queue.offer(src[i])
        }
        assertEquals(src, poll(150))
    }

    override fun size_is_1_WHEN_100_offers_50_polls_100_offers_149_polls() {
        val src = items(150)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 150) {
            queue.offer(src[i])
        }
        poll(149)
        assertEquals(1, queue.size)
    }

    override fun size_is_0_WHEN_100_offers_50_polls_100_offers_150_polls() {
        val src = items(150)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 150) {
            queue.offer(src[i])
        }
        poll(150)
        assertEquals(0, queue.size)
    }

    override fun size_is_0_after_clear() {
        offer(100)
        queue.clear()
        assertEquals(0, queue.size)
    }

    override fun size_is_1_after_clear_and_1_offer() {
        offer(100)
        queue.clear()
        queue.offer("a")
        assertEquals(1, queue.size)
    }

    override fun poll_returns_Null_after_clear() {
        offer(100)
        queue.clear()
        assertNull(queue.poll())
    }

    override fun poll_returns_correct_item_after_clear_and_one_offer() {
        offer(100)
        queue.clear()
        queue.offer("a")
        assertEquals("a", queue.poll())
    }

    override fun size_is_0_after_clear_one_offer_and_one_poll() {
        offer(List(100, Int::toString))
        queue.clear()
        queue.offer("a")
        queue.poll()
        assertEquals(0, queue.size)
    }

    override fun poll_returns_Null_after_clear_one_offer_and_one_poll() {
        offer(100)
        queue.clear()
        queue.offer("a")
        queue.poll()
        assertNull(queue.poll())
    }

    override fun size_is_one_after_initial_capacity_Minus_1_offers_and_same_polls_and_one_additional_offer() {
        val count = INITIAL_CAPACITY - 1
        offer(count)
        poll(count)
        queue.offer("a")
        assertEquals(1, queue.size)
    }

    override fun size_is_initial_capacity_after_same_offers() {
        offer(INITIAL_CAPACITY)
        assertEquals(INITIAL_CAPACITY, queue.size)
    }

    override fun size_is_initial_capacity_plus_1_after_same_offers() {
        offer(INITIAL_CAPACITY + 1)
        assertEquals(INITIAL_CAPACITY + 1, queue.size)
    }

    override fun size_is_initial_capacity_after_initial_capacity_offers_and_two_polls_and_two_offers() {
        offer(INITIAL_CAPACITY)
        poll(2)
        offer(2)
        assertEquals(INITIAL_CAPACITY, queue.size)
    }

    private fun items(count: Int): List<String> = List(count, Int::toString)

    private fun offer(iterable: Iterable<String?>) {
        iterable.forEach(queue::offer)
    }

    private fun offer(count: Int) {
        offer(items(count))
    }

    private fun poll(count: Int): List<String?> = List(count) { queue.poll() }
}
