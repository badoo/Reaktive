package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.isError
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.observable.values
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterTest : ObservableToObservableTests by ObservableToObservableTests<Unit>({ filter { true } }) {

    private val upstream = TestObservable<Int?>()
    private val observer = upstream.filter { it == null || it > 0 }.test()

    @Test
    fun pass_value_WHEN_predicate_allows_this_value() {
        upstream.onNext(1)

        assertEquals(listOf(1), observer.values)
    }

    @Test
    fun pass_null_WHEN_predicate_allows_null() {
        upstream.onNext(null)

        assertEquals(listOf(null), observer.values)
    }

    @Test
    fun filter_value_WHEN_predicate_does_not_allow_this_value() {
        upstream.onNext(-1)

        assertEquals(emptyList(), observer.values)
    }

    @Test
    fun filter_values_WHEN_stream_of_values_is_emitted() {
        upstream.onNext(null, -1, 0, 1)

        assertEquals(listOf(null, 1), observer.values)
    }

    @Test
    fun not_disposed_WHEN_predicate_passes_value() {
        upstream.onNext(1)

        assertFalse(observer.isDisposed)
    }

    @Test
    fun not_disposed_WHEN_predicate_does_not_pass_value() {
        upstream.onNext(-1)

        assertFalse(observer.isDisposed)
    }

    @Test
    fun produces_error_WHEN_predicate_throws_an_exception() {
        val error = Throwable()

        val observer = upstream.filter { throw error }.test()
        upstream.onNext(0)

        assertTrue(observer.isError(error))
    }
}