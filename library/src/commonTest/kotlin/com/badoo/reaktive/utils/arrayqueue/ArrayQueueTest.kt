package com.badoo.reaktive.utils.arrayqueue

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ArrayQueueTest {

    private val queue = ArrayQueue<String?>()

    @Test
    fun `initial size is zero`() {
        assertEquals(0, queue.size)
    }

    @Test
    fun `size is 1 when offer one item`() {
        queue.offer("a")
        assertEquals(1, queue.size)
    }

    @Test
    fun `size is 0 when offer and poll`() {
        queue.offer("a")
        queue.poll()
        assertEquals(0, queue.size)
    }

    @Test
    fun `size is 100 when offer one 100 items`() {
        repeat(100) {
            queue.offer("a")
        }
        assertEquals(100, queue.size)
    }

    @Test
    fun `offer, poll returns value`() {
        queue.offer("a")
        assertEquals("a", queue.poll())
    }

    @Test
    fun `offer, all peeks return same value`() {
        queue.offer("a")
        repeat(50) {
            assertEquals("a", queue.peek)
        }
    }

    @Test
    fun `offer, poll, peak returns null`() {
        queue.offer("a")
        queue.poll()
        assertNull(queue.peek)
    }

    @Test
    fun `offer, second and other polls return null`() {
        queue.offer("a")
        queue.poll()
        repeat(50) {
            assertNull(queue.poll())
        }
    }

    @Test
    fun `offer, poll, poll, offer, poll returns last item`() {
        queue.offer("a")
        poll(2)
        queue.offer("b")
        assertEquals("b", queue.poll())
    }

    @Test
    fun `offer, poll, poll, offer, peek returns last item`() {
        queue.offer("a")
        poll(2)
        queue.offer("b")
        assertEquals("b", queue.peek)
    }

    @Test
    fun `100 offers 100 polls return valid items`() {
        val src = List(100, Int::toString)
        offer(src)
        assertEquals(src, poll(100))
    }

    @Test
    fun `100 offers 50 polls 50 offers 100 polls return valid items`() {
        val src = List(100, Int::toString)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 100) {
            queue.offer(src[i])
        }
        assertEquals(src, poll(100))
    }

    @Test
    fun `size is 1 when 100 offers, 50 polls, 50 offers, 99 polls`() {
        val src = List(100, Int::toString)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 100) {
            queue.offer(src[i])
        }
        poll(99)
        assertEquals(1, queue.size)
    }

    @Test
    fun `size is 0 when 100 offers, 50 polls, 50 offers, 100 polls`() {
        val src = List(100, Int::toString)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 100) {
            queue.offer(src[i])
        }
        poll(100)
        assertEquals(0, queue.size)
    }

    @Test
    fun `100 offers, 50 polls, 100 offers, 150 polls return valid items`() {
        val src = List(150, Int::toString)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 150) {
            queue.offer(src[i])
        }
        assertEquals(src, poll(150))
    }

    @Test
    fun `size is 1 when 100 offers, 50 polls, 100 offers, 149 polls`() {
        val src = List(150, Int::toString)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 150) {
            queue.offer(src[i])
        }
        poll(149)
        assertEquals(1, queue.size)
    }

    @Test
    fun `size is 0 when 100 offers, 50 polls, 100 offers, 150 polls`() {
        val src = List(150, Int::toString)
        repeat(50) { queue.offer("a") }
        repeat(50) { queue.offer(src[it]) }
        poll(50)
        for (i in 50 until 150) {
            queue.offer(src[i])
        }
        poll(150)
        assertEquals(0, queue.size)
    }

    private fun offer(iterable: Iterable<String?>) {
        iterable.forEach(queue::offer)
    }

    private fun poll(count: Int): List<String?> = List(count) { queue.poll() }
}