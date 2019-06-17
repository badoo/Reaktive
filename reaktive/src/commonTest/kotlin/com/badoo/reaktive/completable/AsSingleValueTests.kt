package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.single.value
import kotlin.test.Test
import kotlin.test.assertEquals

class AsSingleValueTests : CompletableToSingleTests by CompletableToSingleTests({ asSingle(0) }) {

    @Test
    fun succeeds_with_default_value_WHEN_upstream_completed() {
        val upstream = TestCompletable()
        val observer = upstream.asSingle(0).test()

        upstream.onComplete()

        assertEquals(0, observer.value)
    }
}