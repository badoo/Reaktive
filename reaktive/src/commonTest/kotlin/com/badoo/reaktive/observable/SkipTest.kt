package com.badoo.reaktive.observable

import com.badoo.reaktive.testutils.getOnNextValues
import com.badoo.reaktive.testutils.test
import kotlin.test.Test
import kotlin.test.assertEquals

class SkipTest {
    @Test
    fun `should skip n values`() {
        val observer = observableOf(1, 2, 3, 4)
            .skip(2)
            .test()
        assertEquals(3, observer.events.size)
        assertEquals(listOf(3, 4), observer.getOnNextValues())
    }

    @Test
    fun `should skip null`() {
        val observer = observableOf(null, null, null, 4)
            .skip(2)
            .test()
        assertEquals(3, observer.events.size)
        assertEquals(listOf(null, 4), observer.getOnNextValues())
    }
}