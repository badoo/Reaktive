package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observableOfEmpty
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test
import kotlin.test.assertTrue

class FlatMapObservableTest : SingleToObservableTests by SingleToObservableTestsImpl({ flatMapObservable { observableOfEmpty<Nothing>() } }) {

    private val upstream = TestSingle<Int?>()

    @Test
    fun does_not_complete_WHEN_upstream_succeeded_with_null_value() {
        val observer = test { TestObservable() }

        upstream.onSuccess(null)

        observer.assertNotComplete()
    }

    @Test
    fun does_not_complete_WHEN_upstream_succeeded_with_non_null_value() {
        val observer = test { TestObservable() }

        upstream.onSuccess(0)

        observer.assertNotComplete()
    }

    @Test
    fun subscribes_to_inner_WHEN_upstream_succeeded_with_null_value() {
        val inner = TestObservable<Int?>()
        test { inner }

        upstream.onSuccess(null)

        assertTrue(inner.hasSubscribers)
    }

    @Test
    fun subscribes_to_inner_WHEN_upstream_succeeded_with_non_value() {
        val inner = TestObservable<Int?>()
        test { inner }

        upstream.onSuccess(0)

        assertTrue(inner.hasSubscribers)
    }

    @Test
    fun produces_values_in_correct_order_WHEN_inner_produced_values() {
        val inner = TestObservable<Int?>()
        val observer = test { inner }
        upstream.onSuccess(0)

        inner.onNext(0, null, 1, 2)

        observer.assertValues(0, null, 1, 2)
    }

    @Test
    fun completes_WHEN_inner_completed() {
        val inner = TestObservable<Int?>()
        val observer = test { inner }
        upstream.onSuccess(0)

        inner.onComplete()

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_inner_produced_error() {
        val error = Exception()
        val inner = TestObservable<Int?>()
        val observer = test { inner }
        upstream.onSuccess(0)

        inner.onError(error)

        observer.assertError(error)
    }

    private fun test(block: (Int?) -> Observable<Int?>): TestObservableObserver<Int?> =
        upstream.flatMapObservable(block).test()
}
