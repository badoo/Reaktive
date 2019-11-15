package com.badoo.reaktive.observable

import com.badoo.reaktive.observable.flatMapIterable
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class FlatMapIterableTest
    : ObservableToObservableTests by ObservableToObservableTests<Unit>({ flatMapIterable { listOf(Unit) } }) {

    private val upstream = TestObservable<() -> List<Int>>()
    private val observer = upstream.flatMapIterable { it() }.test()

    @Test
    fun emits_values_WHEN_upstream_succeeded() {
        upstream.onNext { listOf(1, 2, 3, 4, 5) }

        observer.assertValues(1, 2, 3, 4, 5)
    }
}
