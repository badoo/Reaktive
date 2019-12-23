package com.badoo.reaktive.utils.queue

import com.badoo.reaktive.utils.freeze
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedQueueTest {

    @Test
    fun adds_items_after_freeze() {
        val queue = SharedQueue<Item>()

        queue.offer(Item(1))
        queue.freeze()
        queue.offer(Item(2))

        assertEquals(listOf(Item(1), Item(2)), queue.toList())
    }

    private data class Item(
        val value: Int
    )
}
