package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.singleOf
import com.badoo.reaktive.single.singleOfError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class TestAwaitTest {

    @Test
    fun succeeds_WHEN_upstream_succeeded_and_empty_assertSuccess() =
        singleOf(1)
            .testAwait(assertSuccess = {})

    @Test
    fun succeeds_WHEN_upstream_succeeded_and_assertSuccess_did_not_throw() =
        singleOf(1)
            .testAwait(assertSuccess = { assertEquals(1, it) })

    @Test
    fun succeeds_WHEN_upstream_failed_and_assertError_did_not_throw() {
        val error = Exception()

        return singleOfError<Nothing>(error)
            .testAwait(assertError = { assertSame(error, it) }, assertSuccess = {})
    }
}
