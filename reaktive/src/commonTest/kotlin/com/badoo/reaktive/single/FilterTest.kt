package com.badoo.reaktive.single

import com.badoo.reaktive.test.maybe.isComplete
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.maybe.value
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.isError
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FilterTest :
    UpstreamDownstreamGenericTests by UpstreamDownstreamGenericTests.TransformToMaybe<Unit>({ filter { true } }) {

    private val upstream = TestSingle<Int?>()
    private val observer = upstream.filter { it == null || it > 0 }.test()

    @Test
    fun pass_value_WHEN_predicate_allows_this_value() {
        upstream.onSuccess(1)

        assertEquals(1, observer.value)
    }

    @Test
    fun pass_null_WHEN_predicate_allows_null() {
        upstream.onSuccess(null)

        assertEquals(null, observer.value)
    }

    @Test
    fun filter_value_WHEN_predicate_does_not_allow_this_value() {
        upstream.onSuccess(0)

        assertTrue(observer.isComplete)
    }

    @Test
    fun produces_error_WHEN_predicate_throws_an_exception() {
        val error = Throwable()

        val observer = upstream.map { throw error }.test()
        upstream.onSuccess(1)

        assertTrue(observer.isError(error))
    }
}