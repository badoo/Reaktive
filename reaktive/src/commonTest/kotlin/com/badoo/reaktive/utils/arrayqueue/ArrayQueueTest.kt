package com.badoo.reaktive.utils.arrayqueue

import com.badoo.reaktive.utils.queue.ArrayQueue
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ArrayQueueTest {

    private val queue = ArrayQueue<String?>()

    @Test
    @JsName("initialSizeIsZero")
    fun `initial size is zero`() {
        assertEquals(0, queue.size)
    }

    @Test
    @JsName("sizeIs1WhenOfferOneItem")
    fun `size is 1 when offer one item`() {
        queue.offer("a")
        assertEquals(1, queue.size)
    }

    @Test
    @JsName("sizeIs0WhenOfferAndPoll")
    fun `size is 0 when offer and poll`() {
        queue.offer("a")
        queue.poll()
        assertEquals(0, queue.size)
    }

    @Test
    @JsName("sizeIs100WhenOfferOne100Items")
    fun `size is 100 when offer one 100 items`() {
        repeat(100) {
            queue.offer("a")
        }
        assertEquals(100, queue.size)
    }

    @Test
    @JsName("offerPollReturnsValue")
    fun `offer, poll returns value`() {
        queue.offer("a")
        assertEquals("a", queue.poll())
    }

    @Test
    @JsName("offerAllPeeksReturnSameValue")
    fun `offer, all peeks return same value`() {
        queue.offer("a")
        repeat(50) {
            assertEquals("a", queue.peek)
        }
    }

    @Test
    @JsName("offerPollPeakReturnsNull")
    fun `offer, poll, peak returns null`() {
        queue.offer("a")
        queue.poll()
        assertNull(queue.peek)
    }

    @Test
    @JsName("offerSecondAndOtherPollsReturnNull")
    fun `offer, second and other polls return null`() {
        queue.offer("a")
        queue.poll()
        repeat(50) {
            assertNull(queue.poll())
        }
    }

    @Test
    @JsName("offerPollPollOfferPollReturnsLastItem")
    fun `offer, poll, poll, offer, poll returns last item`() {
        queue.offer("a")
        poll(2)
        queue.offer("b")
        assertEquals("b", queue.poll())
    }

    @Test
    @JsName("offerPollPollOfferPeekReturnsLastItem")
    fun `offer, poll, poll, offer, peek returns last item`() {
        queue.offer("a")
        poll(2)
        queue.offer("b")
        assertEquals("b", queue.peek)
    }

    @Test
    @JsName("a100Offers100PollsReturnValidItems")
    fun `100 offers 100 polls return valid items`() {
        val src = List(100, Int::toString)
        offer(src)
        assertEquals(src, poll(100))
    }

    @Test
    @JsName("a100Offers50Polls50Offers100PollsReturnValidItems")
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
    @JsName("sizeIs1When100Offers50Polls50Offers99Polls")
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
    @JsName("sizeIs0When100Offers50Polls50Offers100Polls")
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
    @JsName("a100Offers50Polls100Offers150PollsReturnValidItems")
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
    @JsName("sizeIs1When100Offers50Polls100Offers149Polls")
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
    @JsName("sizeIs0When100Offers50Polls100Offers150Polls")
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

    @Test
    @JsName("sizeIs0AfterClear")
    fun `size is 0 after clear`() {
        offer(List(100, Int::toString))
        queue.clear()
        assertEquals(0, queue.size)
    }

    @Test
    @JsName("sizeIs1AfterClearAnd1Offer")
    fun `size is 1 after clear and 1 offer`() {
        offer(List(100, Int::toString))
        queue.clear()
        queue.offer("a")
        assertEquals(1, queue.size)
    }

    @Test
    @JsName("pollReturnsNullAfterClear")
    fun `poll returns null after clear`() {
        offer(List(100, Int::toString))
        queue.clear()
        assertNull(queue.poll())
    }

    @Test
    @JsName("pollReturnsCorrectItemAfterClearAndOneOffer")
    fun `poll returns correct item after clear and one offer`() {
        offer(List(100, Int::toString))
        queue.clear()
        queue.offer("a")
        assertEquals("a", queue.poll())
    }

    @Test
    @JsName("sizeIs0AfterClearOneOfferAndOnePoll")
    fun `size is 0 after clear, one offer and one poll`() {
        offer(List(100, Int::toString))
        queue.clear()
        queue.offer("a")
        queue.poll()
        assertEquals(0, queue.size)
    }

    @Test
    @JsName("pollReturnsNullAfterClearOneOfferAndOnePoll")
    fun `poll returns null after clear, one offer and one poll`() {
        offer(List(100, Int::toString))
        queue.clear()
        queue.offer("a")
        queue.poll()
        assertNull(queue.poll())
    }

    private fun offer(iterable: Iterable<String?>) {
        iterable.forEach(queue::offer)
    }

    private fun poll(count: Int): List<String?> = List(count) { queue.poll() }
}