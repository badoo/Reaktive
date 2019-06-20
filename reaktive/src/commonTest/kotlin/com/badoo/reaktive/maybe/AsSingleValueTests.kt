package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.single.value
import kotlin.test.Test
import kotlin.test.assertEquals

class AsSingleValueTests : MaybeToSingleTests by MaybeToSingleTests<Unit>({ asSingle(Unit) }) {

    private val upstream = TestMaybe<Int?>()
    private val observer = upstream.asSingle(-1).test()

    @Test
    fun succeeds_with_upstream_value_WHEN_upstream_succeeded_with_not_null() {
        upstream.onSuccess(0)

        assertEquals(0, observer.value)
    }

    @Test
    fun succeeds_with_null_value_WHEN_upstream_succeeded_with_null() {
        upstream.onSuccess(null)

        assertEquals(null, observer.value)
    }

    @Test
    fun succeeds_with_default_value_WHEN_upstream_completed() {
        upstream.onComplete()

        assertEquals(-1, observer.value)
    }
}