package com.badoo.reaktive.utils.queue

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ArrayQueueTest {

    private val queue = ArrayQueue<String?>()

    @Test
    fun initialSizeIsZero() {
        assertEquals(0, queue.size)
    }

    @Test
    fun empty_after_creation() {
        assertTrue(queue.isEmpty)
    }

    @Test
    fun not_empty_WHEN_offered_one_item() {
        queue.offer("a")

        assertFalse(queue.isEmpty)
    }

    @Test
    fun empty_WHEN_offered_and_polled_one_item() {
        queue.offer("a")
        queue.poll()

        assertTrue(queue.isEmpty)
    }

    @Test
    fun not_empty_WHEN_offered_100_items() {
        repeat(100) {
            queue.offer("a")
        }

        assertFalse(queue.isEmpty)
    }

    @Test
    fun empty_WHEN_offered_and_polled_100_items() {
        repeat(100) {
            queue.offer("a")
        }
        repeat(100) {
            queue.poll()
        }

        assertTrue(queue.isEmpty)
    }

    @Test
    fun not_empty_WHEN_offered_100_items_and_polled_99_items() {
        repeat(100) {
            queue.offer("a")
        }
        repeat(99) {
            queue.poll()
        }

        assertFalse(queue.isEmpty)
    }

    @Test
    fun sizeIs1WhenOfferOneItem() {
        queue.offer("a")
        assertEquals(1, queue.size)
    }

    @Test
    fun sizeIs0WhenOfferAndPoll() {
        queue.offer("a")
        queue.poll()
        assertEquals(0, queue.size)
    }

    @Test
    fun sizeIs100WhenOfferOne100Items() {
        repeat(100) {
            queue.offer("a")
        }
        assertEquals(100, queue.size)
    }

    @Test
    fun offerPollReturnsValue() {
        queue.offer("a")
        assertEquals("a", queue.poll())
    }

    @Test
    fun offerAllPeeksReturnSameValue() {
        queue.offer("a")
        repeat(50) {
            assertEquals("a", queue.peek)
        }
    }

    @Test
    fun offerPollPeekReturnsNull() {
        queue.offer("a")
        queue.poll()
        assertNull(queue.peek)
    }

    @Test
    fun offerSecondAndOtherPollsReturnNull() {
        queue.offer("a")
        queue.poll()
        repeat(50) {
            assertNull(queue.poll())
        }
    }

    @Test
    fun offerPollPollOfferPollReturnsLastItem() {
        queue.offer("a")
        poll(2)
        queue.offer("b")
        assertEquals("b", queue.poll())
    }

    @Test
    fun offerPollPollOfferPeekReturnsLastItem() {
        queue.offer("a")
        poll(2)
        queue.offer("b")
        assertEquals("b", queue.peek)
    }

    @Test
    fun a100Offers100PollsReturnValidItems() {
        val src = List(100, Int::toString)
        offer(src)
        assertEquals(src, poll(100))
    }

    @Test
    fun a100Offers50Polls50Offers100PollsReturnValidItems() {
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
    fun sizeIs1When100Offers50Polls50Offers99Polls() {
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
    fun sizeIs0When100Offers50Polls50Offers100Polls() {
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
    fun a100Offers50Polls100Offers150PollsReturnValidItems() {
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
    fun sizeIs1When100Offers50Polls100Offers149Polls() {
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
    fun sizeIs0When100Offers50Polls100Offers150Polls() {
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
    fun sizeIs0AfterClear() {
        offer(List(100, Int::toString))
        queue.clear()
        assertEquals(0, queue.size)
    }

    @Test
    fun sizeIs1AfterClearAnd1Offer() {
        offer(List(100, Int::toString))
        queue.clear()
        queue.offer("a")
        assertEquals(1, queue.size)
    }

    @Test
    fun pollReturnsNullAfterClear() {
        offer(List(100, Int::toString))
        queue.clear()
        assertNull(queue.poll())
    }

    @Test
    fun pollReturnsCorrectItemAfterClearAndOneOffer() {
        offer(List(100, Int::toString))
        queue.clear()
        queue.offer("a")
        assertEquals("a", queue.poll())
    }

    @Test
    fun sizeIs0AfterClearOneOfferAndOnePoll() {
        offer(List(100, Int::toString))
        queue.clear()
        queue.offer("a")
        queue.poll()
        assertEquals(0, queue.size)
    }

    @Test
    fun pollReturnsNullAfterClearOneOfferAndOnePoll() {
        offer(List(100, Int::toString))
        queue.clear()
        queue.offer("a")
        queue.poll()
        assertNull(queue.poll())
    }

    @Test
    fun sizeIsOneAfterInitialCapacityMinus1OffersAndSamePollsAndOneAdditionalOffer() {
        val count = ArrayQueue.INITIAL_CAPACITY - 1
        offer(List(count, Int::toString))
        poll(count)
        queue.offer("a")
        assertEquals(1, queue.size)
    }

    @Test
    fun sizeIsInitialCapacityAfterSameOffers() {
        offer(List(ArrayQueue.INITIAL_CAPACITY, Int::toString))
        assertEquals(ArrayQueue.INITIAL_CAPACITY, queue.size)
    }

    @Test
    fun sizeIsInitialCapacityPlus1AfterSameOffers() {
        offer(List(ArrayQueue.INITIAL_CAPACITY + 1, Int::toString))
        assertEquals(ArrayQueue.INITIAL_CAPACITY + 1, queue.size)
    }

    @Test
    fun sizeIsInitialCapacityAfterInitialCapacityOffersAndTwoPollsAndTwoOffers() {
        offer(List(ArrayQueue.INITIAL_CAPACITY, Int::toString))
        poll(2)
        offer(List(2, Int::toString))
        assertEquals(ArrayQueue.INITIAL_CAPACITY, queue.size)
    }


    private fun offer(iterable: Iterable<String?>) {
        iterable.forEach(queue::offer)
    }

    private fun poll(count: Int): List<String?> = List(count) { queue.poll() }
}
