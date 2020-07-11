package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.assertNotComplete
import com.badoo.reaktive.test.completable.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AmbTest {

    private val source1 = TestCompletable()
    private val source2 = TestCompletable()
    private val source3 = TestCompletable()
    private val amb = amb(source1, source2, source3).test()

    @Test
    fun all_sources_subscribed() {
        assertTrue(source1.hasSubscribers)
        assertTrue(source2.hasSubscribers)
        assertTrue(source3.hasSubscribers)
    }

    @Test
    fun winner_can_produce_completion_to_downstream() {
        source2.onComplete()

        amb.assertComplete()
    }

    @Test
    fun winner_can_produce_error_to_downstream() {
        val error = Exception()

        source2.onError(error)

        amb.assertError(error)
    }

    @Test
    fun all_sources_are_disposed_WHEN_first_source_completes() {
        source2.onComplete()

        assertFalse(source1.hasSubscribers)
        assertFalse(source2.hasSubscribers)
        assertFalse(source3.hasSubscribers)
    }

    @Test
    fun all_sources_are_disposed_WHEN_first_source_produces_error() {
        source2.onError(Exception())

        assertFalse(source1.hasSubscribers)
        assertFalse(source2.hasSubscribers)
        assertFalse(source3.hasSubscribers)
    }

    @Test
    fun losers_cant_produce_onComplete_to_downstream() {
        val looserObservers = source1.observers + source3.observers
        source2.onError(Exception())

        looserObservers.forEach {
            it.onComplete()
        }

        amb.assertNotComplete()
    }

    @Test
    fun losers_cant_produce_onError_to_downstream() {
        val looserObservers = source1.observers + source3.observers
        source2.onComplete()

        looserObservers.forEach {
            it.onError(Exception())
        }

        amb.assertNotError()
    }

    @Test
    fun completed_WHEN_sources_are_empty() {
        val observer = emptyList<Completable>().amb().test()

        observer.assertComplete()
    }
}
