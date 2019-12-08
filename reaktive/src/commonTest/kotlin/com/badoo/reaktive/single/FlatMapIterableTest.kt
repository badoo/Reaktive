package com.badoo.reaktive.single

import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test

class FlatMapIterableTest
    : SingleToObservableTests by SingleToObservableTestsImpl({ flatMapIterable { listOf(Unit) } }) {

    private val upstream = TestSingle<List<Int?>>()
    private val observer = upstream.flatMapIterable { it }.test()

    @Test
    fun emits_values_WHEN_upstream_succeeded() {
        upstream.onSuccess(listOf(1, 2, null, 4, 5))

        observer.assertValues(1, 2, null, 4, 5)
    }
}
