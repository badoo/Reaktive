package com.badoo.reaktive.utils.queue

import com.badoo.reaktive.utils.queue.ArrayQueue.Companion.INITIAL_CAPACITY
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayQueueTest : QueueTests by QueueTestsImpl(ArrayQueue()) {

    private val queue = ArrayQueue<String?>()

    @Test
    fun iterator_has_all_items_in_order_WHEN_offered_less_than_INITIAL_CAPACITY_items() {
        val originalList = listOf("a", "b", "c")
        offer(originalList)

        val list = queue.iterator().asSequence().toList()

        assertEquals(originalList, list)
    }

    @Test
    fun iterator_has_all_items_in_order_WHEN_offered_INITIAL_CAPACITY_items() {
        offer(INITIAL_CAPACITY / 2)
        poll(INITIAL_CAPACITY / 2)
        val originalList = items(INITIAL_CAPACITY)
        offer(originalList)

        val list = queue.iterator().asSequence().toList()

        assertEquals(originalList, list)
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
