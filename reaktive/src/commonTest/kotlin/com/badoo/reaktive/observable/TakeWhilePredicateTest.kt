package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class TakeWhilePredicateTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ takeWhile { true } }) {

    private val upstream = TestObservable<Int?>()

    @Test
    fun emits_values_in_correct_order_WHILE_predicate_returns_true() {
        val observer = upstream.takeWhile { it != 2 }.test()

        upstream.onNext(0, null, 1, 2, null, 3, null)

        observer.assertValues(0, null, 1)
    }

    @Test
    fun completes_WHEN_predicate_returns_false() {
        val observer = upstream.takeWhile { false }.test()

        upstream.onNext(0)

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_predicate_thrown_exception() {
        val error = Exception()
        val observer = upstream.takeWhile { throw error }.test()

        upstream.onNext(0)

        observer.assertError(error)
    }
}
