package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.isError
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.single.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AsSingleOrErrorSupplierTests : MaybeToSingleTests by MaybeToSingleTests<Unit>({ asSingle(::Throwable) }) {

    private val upstream = TestMaybe<Int?>()
    private val error = Exception()
    private val observer = asSingleOrErrorSubscribe(error)

    // To avoid freezing of "this"
    private fun asSingleOrErrorSubscribe(error: Exception): TestSingleObserver<Int?> =
        upstream.asSingleOrError { error }.test()

    @Test
    fun succeeds_with_upstream_value_WHEN_upstream_succeeded_with_non_null() {
        upstream.onSuccess(0)

        assertEquals(0, observer.value)
    }

    @Test
    fun succeeds_with_null_value_WHEN_upstream_succeeded_with_null() {
        upstream.onSuccess(null)

        assertEquals(null, observer.value)
    }

    @Test
    fun produces_error_WHEN_upstream_completed() {
        upstream.onComplete()

        assertTrue(observer.isError(error))
    }

    @Test
    fun produces_error_WHEN_supplier_throws_exception() {
        val exception = Exception()

        val observer =
            upstream
                .asSingleOrError { throw exception }
                .test()

        upstream.onComplete()

        assertTrue(observer.isError(exception))
    }
}