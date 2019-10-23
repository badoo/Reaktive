package com.badoo.reaktive.single

import com.badoo.reaktive.scheduler.computationScheduler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BlockingGetTest {

    @Test
    fun returns_non_null_value() {
        val upstream = singleOf(0).subscribeOn(computationScheduler)

        val result = upstream.blockingGet()

        assertEquals(0, result)
    }

    @Test
    fun returns_null_value() {
        val upstream = singleOf(null).subscribeOn(computationScheduler)

        val result = upstream.blockingGet()

        assertNull(result)
    }

    @Test
    fun throws_exception_WHEN_upstream_produced_error() {
        val upstream = singleOfError<Nothing>(Exception("Error")).subscribeOn(computationScheduler)

        assertFailsWith<Exception>("Error") {
            upstream.blockingGet()
        }
    }
}
