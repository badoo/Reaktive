package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test

class AsSingleOrErrorValueTests : MaybeToSingleTests by MaybeToSingleTestsImpl({ asSingleOrError(Throwable()) }) {

    private val upstream = TestMaybe<Int?>()
    private val error = Exception()
    private val observer = upstream.asSingleOrError(error).test()

    @Test
    fun succeeds_with_upstream_value_WHEN_upstream_succeeded_with_non_null() {
        upstream.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_with_null_value_WHEN_upstream_succeeded_with_null() {
        upstream.onSuccess(null)

        observer.assertSuccess(null)
    }

    @Test
    fun produces_error_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertError(error)
    }
}
