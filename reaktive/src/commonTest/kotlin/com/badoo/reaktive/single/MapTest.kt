package com.badoo.reaktive.single

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test

class MapTest : SingleToSingleTests by SingleToSingleTestsImpl({ map {} }) {

    private val upstream = TestSingle<String?>()
    private val observer = upstream.map { it?.length }.test()

    @Test
    fun maps_non_null_value() {
        upstream.onSuccess("abc")

        observer.assertSuccess(3)
    }

    @Test
    fun maps_null_value() {
        upstream.onSuccess(null)

        observer.assertSuccess(null)
    }

    @Test
    fun produces_error_WHEN_mapper_throws_an_exception() {
        val error = Throwable()

        val observer = upstream.map { throw error }.test()
        upstream.onSuccess("abc")

        observer.assertError(error)
    }
}
