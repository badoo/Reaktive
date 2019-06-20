package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.isComplete
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.maybe.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotNullTest : MaybeToMaybeTests by MaybeToMaybeTests<Unit>({ notNull() }) {

    private val upstream = TestMaybe<Int?>()
    private val observer = upstream.notNull().test()

    @Test
    fun passes_not_null_value() {
        upstream.onSuccess(0)

        assertEquals(0, observer.value)
    }

    @Test
    fun filters_null_value() {
        upstream.onSuccess(null)

        assertTrue(observer.isComplete)
    }
}