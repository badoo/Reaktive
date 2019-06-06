package com.badoo.reaktive.single

import com.badoo.reaktive.test.maybe.isComplete
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.maybe.value
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotNullTest : SingleToMaybeTests by SingleToMaybeTests.Companion<Unit>({ notNull() }) {

    private val upstream = TestSingle<Int?>()
    private val observer = upstream.notNull().test()

    @Test
    fun pass_not_null_value() {
        upstream.onSuccess(0)

        assertEquals(0, observer.value)
    }

    @Test
    fun filter_null_value() {
        upstream.onSuccess(null)

        assertTrue(observer.isComplete)
    }
}