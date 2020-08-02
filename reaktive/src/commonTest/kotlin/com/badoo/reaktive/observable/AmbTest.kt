package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AmbTest {

    private val source1 = TestObservable<Int?>()
    private val source2 = TestObservable<Int?>()
    private val source3 = TestObservable<Int?>()
    private val amb = amb(source1, source2, source3).test()

    @Test
    fun all_sources_subscribed() {
        assertTrue(source1.hasSubscribers)
        assertTrue(source2.hasSubscribers)
        assertTrue(source3.hasSubscribers)
    }

    @Test
    fun winner_can_produce_values_to_downstream() {
        source2.onNext(0, null, 1, null, 2)

        amb.assertValues(0, null, 1, null, 2)
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
    fun winner_remains_subscribed() {
        source2.onNext(0)

        assertTrue(source2.hasSubscribers)
    }

    @Test
    fun all_losers_are_disposed_WHEN_first_source_emits_value() {
        source2.onNext(0)

        assertFalse(source1.hasSubscribers)
        assertFalse(source3.hasSubscribers)
    }

    @Test
    fun all_losers_are_disposed_WHEN_first_source_completes() {
        source2.onComplete()

        assertFalse(source1.hasSubscribers)
        assertFalse(source3.hasSubscribers)
    }

    @Test
    fun all_losers_are_disposed_WHEN_first_source_produces_error() {
        source2.onError(Exception())

        assertFalse(source1.hasSubscribers)
        assertFalse(source3.hasSubscribers)
    }

    @Test
    fun losers_cant_emit_values() {
        val looserObservers = source1.observers + source3.observers
        source2.onNext(0)

        looserObservers.forEach {
            it.onNext(1)
        }

        assertFalse(amb.values.contains(1))
    }

    @Test
    fun losers_cant_produce_onComplete_to_downstream() {
        val looserObservers = source1.observers + source3.observers
        source2.onNext(0)

        looserObservers.forEach {
            it.onComplete()
        }

        amb.assertNotComplete()
    }

    @Test
    fun losers_cant_produce_onError_to_downstream() {
        val looserObservers = source1.observers + source3.observers
        source2.onNext(0)

        looserObservers.forEach {
            it.onError(Exception())
        }

        amb.assertNotError()
    }

    @Test
    fun completed_WHEN_sources_are_empty() {
        val observer = emptyList<Observable<Any>>().amb().test()

        observer.assertComplete()
    }
}
