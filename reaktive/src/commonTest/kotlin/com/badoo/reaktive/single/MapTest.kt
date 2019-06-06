package com.badoo.reaktive.single

import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.isError
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.single.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapTest : SingleToSingleTests by SingleToSingleTests<Unit>({ map {} }) {

    private val upstream = TestSingle<String?>()
    private val observer = upstream.map { it?.length }.test()

    @Test
    fun maps_non_null_value() {
        upstream.onSuccess("abc")

        assertEquals(3, observer.value)
    }

    @Test
    fun maps_null_value() {
        upstream.onSuccess(null)

        assertEquals(null, observer.value)
    }

    @Test
    fun produces_error_WHEN_mapper_throws_an_exception() {
        val error = Throwable()

        val observer = upstream.map { throw error }.test()
        upstream.onSuccess("abc")

        assertTrue(observer.isError(error))
    }
}