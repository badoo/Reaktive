package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.single.isError
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.single.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AsSingleSupplierTests : CompletableToSingleTests by CompletableToSingleTests({ asSingle { 0 } }) {

    val upstream = TestCompletable()

    @Test
    fun succeeds_with_default_value_WHEN_upstream_completed() {
        val observer = upstream.asSingle { 0 }.test()
        upstream.onComplete()

        assertEquals(0, observer.value)
    }

    @Test
    fun produces_error_WHEN_supplier_throws_exception() {
        val upstream = TestCompletable()
        val exception = Exception()

        val observer =
            upstream
                .asSingle { throw exception }
                .test()

        upstream.onComplete()

        assertTrue(observer.isError(exception))
    }
}