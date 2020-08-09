package com.badoo.reaktive.single

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AsPromiseTest {

    @Test
    fun resolves_non_null_value(): dynamic =
        singleOf(1)
            .asPromise()
            .then { assertEquals(1, it) }

    @Test
    fun resolves_null_value(): dynamic =
        singleOf<Int?>(null)
            .asPromise()
            .then { assertEquals(null, it) }

    @Test
    fun rejects(): dynamic {
        val error = Exception()

        return singleOfError<Nothing>(error)
            .asPromise()
            .catch { it }
            .then { assertSame(error, it) }
    }
}
