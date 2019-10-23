package com.badoo.reaktive.maybe

import com.badoo.reaktive.scheduler.computationScheduler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BlockingGetTest {

    @Test
    fun returns_non_null_value() {
        val upstream = maybeOf(0).subscribeOn(computationScheduler)

        val result = upstream.blockingGet()

        assertEquals(0, result)
    }

    @Test
    fun returns_null_value_WHEN_succeeded_with_null() {
        val upstream = maybeOf(null).subscribeOn(computationScheduler)

        val result = upstream.blockingGet()

        assertNull(result)
    }

    @Test
    fun returns_null_value_WHEN_completed() {
        val upstream = maybeOfEmpty<Nothing>().subscribeOn(computationScheduler)

        val result = upstream.blockingGet()

        assertNull(result)
    }

    @Test
    fun throws_exception_WHEN_upstream_produced_error() {
        val upstream = maybeOfError<Nothing>(Exception("Error")).subscribeOn(computationScheduler)

        assertFailsWith<Exception>("Error") {
            upstream.blockingGet()
        }
    }
}
