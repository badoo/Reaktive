package com.badoo.reaktive.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class PairReferenceTest {

    @Test
    fun returns_initial_values_WHEN_created() {
        val ref = PairReference(VALUE_1, VALUE_2)

        assertEquals(VALUE_1, ref.first)
        assertEquals(VALUE_2, ref.second)
    }

    @Test
    fun returns_updated_values() {
        val ref = PairReference(VALUE_1, VALUE_2)

        ref.first = VALUE_3
        ref.second = VALUE_4

        assertEquals(VALUE_3, ref.first)
        assertEquals(VALUE_4, ref.second)
    }

    private companion object {
        private const val VALUE_1 = "a"
        private const val VALUE_2 = "b"
        private const val VALUE_3 = "c"
        private const val VALUE_4 = "d"
    }
}
