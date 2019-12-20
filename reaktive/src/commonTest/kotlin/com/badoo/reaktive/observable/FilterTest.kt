package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotDisposed
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class FilterTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ filter { true } }) {

    private val upstream = TestObservable<Int?>()
    private val observer = upstream.filter { it == null || it > 0 }.test()

    @Test
    fun pass_value_WHEN_predicate_allows_this_value() {
        upstream.onNext(1)

        observer.assertValue(1)
    }

    @Test
    fun pass_null_WHEN_predicate_allows_null() {
        upstream.onNext(null)

        observer.assertValue(null)
    }

    @Test
    fun filter_value_WHEN_predicate_does_not_allow_this_value() {
        upstream.onNext(-1)

        observer.assertNoValues()
    }

    @Test
    fun filter_values_WHEN_stream_of_values_is_emitted() {
        upstream.onNext(null, -1, 0, 1)

        observer.assertValues(null, 1)
    }

    @Test
    fun not_disposed_WHEN_predicate_passes_value() {
        upstream.onNext(1)

        observer.assertNotDisposed()
    }

    @Test
    fun not_disposed_WHEN_predicate_does_not_pass_value() {
        upstream.onNext(-1)

        observer.assertNotDisposed()
    }

    @Test
    fun produces_error_WHEN_predicate_throws_an_exception() {
        val error = Throwable()

        val observer = upstream.filter { throw error }.test()
        upstream.onNext(0)

        observer.assertError(error)
    }
}
