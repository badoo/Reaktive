package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class FlatMapIterableTest
    : MaybeToObservableTests by MaybeToObservableTestsImpl({ flatMapIterable { listOf(Unit) } }) {

    private val upstream = TestMaybe<List<Int?>>()
    private val observer = upstream.flatMapIterable { it }.test()

    @Test
    fun emits_values_WHEN_upstream_succeeded() {
        upstream.onSuccess(listOf(1, 2, 3, 4, 5))

        observer.assertValues(1, 2, 3, 4, 5)
    }

    @Test
    fun emits_values_WHEN_upstream_produces_null_value() {
        upstream.onSuccess(listOf(null, 1, null))

        observer.assertValues(null, 1, null)
    }
}
