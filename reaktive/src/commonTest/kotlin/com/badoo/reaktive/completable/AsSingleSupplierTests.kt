package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test

class AsSingleSupplierTests : CompletableToSingleTests by CompletableToSingleTestsImpl({ asSingle { 0 } }) {

    val upstream = TestCompletable()

    @Test
    fun succeeds_with_default_value_WHEN_upstream_completed() {
        val observer = upstream.asSingle { 0 }.test()
        upstream.onComplete()

        observer.assertSuccess(0)
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

        observer.assertError(exception)
    }
}
