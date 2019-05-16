package com.badoo.reaktive.observable

import com.badoo.reaktive.testutils.TestObservable
import com.badoo.reaktive.testutils.test
import com.badoo.reaktive.testutils.values
import kotlin.test.Test
import kotlin.test.assertEquals

class SwitchIfEmptyTest: UpstreamDownstreamGenericTests by UpstreamDownstreamGenericTests<Int>({ switchIfEmpty(observableOf(10)) }) {

    @Test
    fun should_switch_streams_when_source_is_empty() {
        val source = TestObservable<Int>()
        val observer = source.switchIfEmpty(observableOf(42)).test()

        source.onComplete()

        assertEquals(2, observer.events.size)
        assertEquals(listOf(42), observer.values)
    }

    @Test
    fun should_not_switch_streams_when_source_isnot_empty() {
        val source = TestObservable<Int>()
        val observer = source.switchIfEmpty(observableOf(42)).test()

        source.onNext(1)
        source.onComplete()

        assertEquals(2, observer.events.size)
        assertEquals(listOf(1), observer.values)
    }

    @Test
    fun should_not_switch_streams_when_source_emits_null() {
        val source = TestObservable<Int?>()
        val observer = source.switchIfEmpty(observableOf(42)).test()

        source.onNext(null)
        source.onComplete()

        assertEquals(2, observer.events.size)
        assertEquals(listOf(null), observer.values)
    }

}