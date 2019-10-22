package com.badoo.reaktive.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectReferenceTest {

    @Test
    fun returns_initial_value_WHEN_created() {
        val ref = ObjectReference(VALUE_1)

        assertEquals(VALUE_1, ref.value)
    }

    @Test
    fun returns_updated_value() {
        val ref = ObjectReference(VALUE_1)

        ref.value = VALUE_2

        assertEquals(VALUE_2, ref.value)
    }

    private companion object {
        private const val VALUE_1 = "a"
        private const val VALUE_2 = "b"
    }
}
