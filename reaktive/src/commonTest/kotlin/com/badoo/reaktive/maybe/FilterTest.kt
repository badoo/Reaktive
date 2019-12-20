package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Test

class FilterTest : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ filter { true } }) {

    private val upstream = TestMaybe<Int?>()
    private val observer = upstream.filter { it == null || it > 0 }.test()

    @Test
    fun pass_value_WHEN_predicate_allows_this_value() {
        upstream.onSuccess(1)

        observer.assertSuccess(1)
    }

    @Test
    fun pass_null_WHEN_predicate_allows_null() {
        upstream.onSuccess(null)

        observer.assertSuccess(null)
    }

    @Test
    fun filter_value_WHEN_predicate_does_not_allow_this_value() {
        upstream.onSuccess(0)

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_predicate_throws_an_exception() {
        val error = Throwable()

        val observer = upstream.map { throw error }.test()
        upstream.onSuccess(1)

        observer.assertError(error)
    }
}
