package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.observable.values
import kotlin.test.Test
import kotlin.test.assertEquals

class SkipTest : UpstreamDownstreamGenericTests by UpstreamDownstreamGenericTests<Unit>({ skip(0) }) {
    @Test
    fun should_skip_n_values() {
        val source = TestObservable<Int>()
        val observer = source
            .skip(2)
            .test()

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        source.onNext(4)

        assertEquals(2, observer.events.size)
        assertEquals(listOf(3, 4), observer.values)
    }

    @Test
    fun should_skip_null() {
        val source = TestObservable<Int?>()
        val observer = source
            .skip(2)
            .test()

        source.onNext(null)
        source.onNext(null)
        source.onNext(null)
        source.onNext(4)

        assertEquals(2, observer.events.size)
        assertEquals(listOf(null, 4), observer.values)
    }
}