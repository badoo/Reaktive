package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.isError
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.maybe.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapTest : MaybeToMaybeTests by MaybeToMaybeTests<Unit>({ map {} }) {

    private val upstream = TestMaybe<String?>()
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