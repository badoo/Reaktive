package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class TakeUntilPredicateTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ takeUntil { false } }) {

    private val upstream = TestObservable<Int?>()

    @Test
    fun emits_values_in_correct_order_UNTIL_predicate_returns_true() {
        val observer = upstream.takeUntil { it == 2 }.test()

        upstream.onNext(0, null, 1, 2, null, 3, null)

        observer.assertValues(0, null, 1, 2)
    }

    @Test
    fun completes_WHEN_predicate_returns_true() {
        val observer = upstream.takeUntil { true }.test()

        upstream.onNext(0)

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_predicate_thrown_exception() {
        val error = Exception()
        val observer = upstream.takeUntil { throw error }.test()

        upstream.onNext(0)

        observer.assertError(error)
    }
}
