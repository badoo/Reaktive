package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class FlatMapIterableTest
    :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ flatMapIterable { listOf(Unit) } }) {

    private val upstream = TestObservable<List<Int?>>()
    private val observer = upstream.flatMapIterable { it }.test()

    @Test
    fun emits_values_WHEN_upstream_produces_values() {
        upstream.onNext(listOf(1, 2, 3, 4, 5))

        observer.assertValues(1, 2, 3, 4, 5)
    }

    @Test
    fun emits_values_WHEN_upstream_produces_list_with_null_value() {
        upstream.onNext(listOf(null, null, 1))

        observer.assertValues(null, null, 1)
    }

    @Test
    fun emits_values_WHEN_upstream_produces_multiple_values() {
        upstream.onNext(listOf(1, 2))
        upstream.onNext(listOf(3, 4))

        observer.assertValues(1, 2, 3, 4)
    }
}
