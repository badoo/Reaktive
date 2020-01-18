package com.badoo.reaktive.utils.queue

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PriorityQueueTest {

    private val queue = PriorityQueue<Int>(compareBy { it })

    @Test
    fun new_instance_has_size_0() {
        assertEquals(0, queue.size)
    }

    @Test
    fun new_instance_is_empty() {
        assertTrue(queue.isEmpty)
    }

    @Test
    fun poll_returns_null_WHEN_new_instance() {
        val item = queue.poll()

        assertNull(item)
    }

    @Test
    fun peek_returns_null_WHEN_new_instance() {
        val item = queue.peek

        assertNull(item)
    }

    @Test
    fun size_is_1_WHEN_offer_1_non_null_item() {
        queue.offer(1)

        assertEquals(1, queue.size)
    }

    @Test
    fun not_empty_WHEN_offer_1_non_null_item() {
        queue.offer(1)

        assertFalse(queue.isEmpty)
    }

    @Test
    fun size_is_100_WHEN_offer_100_items() {
        offer100Items()

        assertEquals(100, queue.size)
    }

    @Test
    fun not_empty_WHEN_offer_100_items() {
        offer100Items()

        assertFalse(queue.isEmpty)
    }

    @Test
    fun poll_returns_same_item_WHEN_offer_1_item() {
        queue.offer(1)

        val item = queue.poll()

        assertEquals(1, item)
    }

    @Test
    fun peek_returns_same_item_WHEN_offer_1_item() {
        queue.offer(1)

        val item = queue.peek

        assertEquals(1, item)
    }

    @Test
    fun size_is_0_WHEN_offer_1_item_and_poll() {
        queue.offer(1)
        queue.poll()

        assertEquals(0, queue.size)
    }

    @Test
    fun empty_WHEN_offer_1_item_and_poll() {
        queue.offer(1)
        queue.poll()

        assertTrue(queue.isEmpty)
    }

    @Test
    fun size_is_0_WHEN_offer_100_items_and_poll_100_items() {
        offer100Items()

        repeat(100) {
            queue.poll()
        }

        assertEquals(0, queue.size)
    }

    @Test
    fun empty_WHEN_offer_100_items_and_poll_100_items() {
        offer100Items()

        repeat(100) {
            queue.poll()
        }

        assertTrue(queue.isEmpty)
    }

    @Test
    fun size_is_99_WHEN_offer_100_items_and_poll_1_item() {
        offer100Items()
        queue.poll()

        assertEquals(99, queue.size)
    }

    @Test
    fun not_empty_WHEN_offer_100_items_and_poll_1_item() {
        offer100Items()
        queue.poll()

        assertFalse(queue.isEmpty)
    }

    @Test
    fun size_is_1_WHEN_offer_100_items_and_poll_99_items() {
        offer100Items()

        repeat(99) {
            queue.poll()
        }

        assertEquals(1, queue.size)
    }

    @Test
    fun not_empty_WHEN_offer_100_items_and_poll_100_items() {
        offer100Items()

        repeat(99) {
            queue.poll()
        }

        assertFalse(queue.isEmpty)
    }

    @Test
    fun poll_returns_prioritized_items() {
        val items = getItemsForTest()

        items.forEach(queue::offer)

        val resultItems = List(items.size) { queue.poll() }

        assertEquals(items.sorted(), resultItems)
    }

    @Test
    fun peek_returns_prioritized_items() {
        val items = getItemsForTest()

        items.forEach(queue::offer)

        val resultItems =
            List(items.size) {
                queue.peek
                queue.poll()
            }

        assertEquals(items.sorted(), resultItems)
    }

    @Test
    fun poll_returns_prioritized_items_WHEN_offer_items_and_poll_half_of_the_items_and_offer_items() {
        val fullItems = getItemsForTest()
        val fullSize = fullItems.size
        val halfSize = fullSize / 2

        fullItems.forEach(queue::offer)

        val firstResult =
            List(halfSize) {
                queue.poll()
            }

        fullItems.forEach(queue::offer)

        val secondResult =
            List(fullSize + halfSize) {
                queue.poll()
            }

        val sortedItems = fullItems.sorted()
        assertEquals(sortedItems.take(halfSize), firstResult)
        assertEquals((sortedItems.takeLast(halfSize) + fullItems).sorted(), secondResult)
    }

    @Test
    fun size_is_0_WHEN_offer_1_item_and_clear() {
        queue.offer(1)
        queue.clear()

        assertEquals(0, queue.size)
    }

    @Test
    fun empty_WHEN_offer_1_item_and_clear() {
        queue.offer(1)
        queue.clear()

        assertTrue(queue.isEmpty)
    }

    @Test
    fun size_is_0_WHEN_offer_100_items_and_clear() {
        repeat(100, queue::offer)
        queue.clear()

        assertEquals(0, queue.size)
    }

    @Test
    fun empty_WHEN_offer_100_items_and_clear() {
        repeat(100, queue::offer)
        queue.clear()

        assertTrue(queue.isEmpty)
    }

    @Test
    fun new_instance_has_empty_iterator() {
        val iterator = queue.iterator()

        assertFalse(iterator.hasNext())
    }

    @Test
    fun iterator_returns_items_in_any_order() {
        val items = getItemsForTest()
        items.forEach(queue::offer)

        val resultItems = queue.iterator().asSequence().toSet()

        assertEquals(items.toSet(), resultItems)
    }

    @Test
    fun iterator_is_empty_WHEN_offer_100_items_and_poll_100_items() {
        offer100Items()

        repeat(100) {
            queue.poll()
        }

        val iterator = queue.iterator()

        assertFalse(iterator.hasNext())
    }

    private fun getItemsForTest(): List<Int> = listOf(5, 10, 3, 6, 4, 3, 6, 10, 2, 3, 1, 5, 50, 0)

    private fun offer100Items() {
        for (i in 0 until 50) {
            queue.offer(i)
        }

        for (i in 99 downTo 50) {
            queue.offer(i)
        }
    }
}
