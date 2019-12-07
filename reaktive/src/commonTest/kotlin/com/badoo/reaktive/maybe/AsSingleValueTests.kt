package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test

class AsSingleValueTests : MaybeToSingleTests by MaybeToSingleTestsImpl({ asSingle(Unit) }) {

    private val upstream = TestMaybe<Int?>()
    private val observer = upstream.asSingle(-1).test()

    @Test
    fun succeeds_with_upstream_value_WHEN_upstream_succeeded_with_not_null() {
        upstream.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_with_null_value_WHEN_upstream_succeeded_with_null() {
        upstream.onSuccess(null)

        observer.assertSuccess(null)
    }

    @Test
    fun succeeds_with_default_value_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertSuccess(-1)
    }
}
