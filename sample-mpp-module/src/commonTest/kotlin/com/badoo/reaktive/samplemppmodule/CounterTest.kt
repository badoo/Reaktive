package com.badoo.reaktive.samplemppmodule

import com.badoo.reaktive.samplemppmodule.Counter.Event
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("TestFunctionName")
class CounterTest {

    private val mainScheduler = TestScheduler()
    private val computationScheduler = TestScheduler(isManualProcessing = true)
    private val counter = Counter()
    private val state get() = counter.state.value

    @BeforeTest
    fun before() {
        overrideSchedulers(
            main = { mainScheduler },
            computation = { computationScheduler },
        )
    }

    @AfterTest
    fun after() {
        overrideSchedulers()
    }

    @Test
    fun WHEN_Event_Increment_THEN_value_incremented_by_1() {
        counter.onEvent(Event.Increment)

        assertEquals(1, state.value)
    }

    @Test
    fun WHEN_Event_Decrement_THEN_value_decremented_by_1() {
        counter.onEvent(Event.Decrement)

        assertEquals(-1, state.value)
    }

    @Test
    fun WHEN_Event_Fibonacci_and_not_processed_THEN_ioLoading_true() {
        counter.onEvent(Event.Fibonacci)

        assertTrue(state.isLoading)
    }

    @Test
    fun WHEN_Event_Fibonacci_and_processed_THEN_value_updated() {
        repeat(10) {
            counter.onEvent(Event.Increment)
        }

        counter.onEvent(Event.Fibonacci)
        computationScheduler.process()

        assertEquals(55, state.value)
    }

    @Test
    fun WHEN_Event_Fibonacci_and_processed_THEN_isLoading_false() {
        repeat(10) {
            counter.onEvent(Event.Increment)
        }

        counter.onEvent(Event.Fibonacci)
        computationScheduler.process()

        assertFalse(state.isLoading)
    }

    @Test
    fun WHEN_Event_Rest_THEN_value_0() {
        counter.onEvent(Event.Increment)

        counter.onEvent(Event.Reset)

        assertEquals(0, state.value)
    }
}
