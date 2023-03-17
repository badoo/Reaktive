package com.badoo.reaktive.utils.atomic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AtomicReferenceTest {

    @Test
    fun returns_initial_value_WHEN_created() {
        val ref = AtomicReference(VALUE_1)

        assertEquals(VALUE_1, ref.value)
    }

    @Test
    fun returns_updated_value() {
        val ref = AtomicReference(VALUE_1)

        ref.value = VALUE_2

        assertEquals(VALUE_2, ref.value)
    }

    @Test
    fun compareAndSet_success() {
        val ref = AtomicReference(VALUE_1)
        val result = ref.compareAndSet(VALUE_1, VALUE_2)
        assertTrue(result)
        assertSame(VALUE_2, ref.value)
    }

    @Test
    fun compareAndSet_fail() {
        val ref = AtomicReference(VALUE_1)
        val result = ref.compareAndSet(VALUE_2, VALUE_3)
        assertFalse(result)
        assertSame(VALUE_1, ref.value)
    }

    @Test
    fun getAndUpdate() {
        val ref = AtomicReference(VALUE_1)
        val result = ref.getAndUpdate { VALUE_2 }
        assertSame(VALUE_1, result)
        assertSame(VALUE_2, ref.value)
    }

    @Test
    fun updateAndGet() {
        val ref = AtomicReference(VALUE_1)
        val result = ref.updateAndGet { VALUE_2 }
        assertSame(VALUE_2, result)
        assertSame(VALUE_2, ref.value)
    }

    @Test
    fun getAndSet() {
        val ref = AtomicReference(VALUE_1)
        val result = ref.getAndSet(VALUE_2)
        assertSame(VALUE_1, result)
        assertSame(VALUE_2, ref.value)
    }

    @Test
    fun update() {
        val ref = AtomicReference(VALUE_1)
        ref.update { VALUE_2 }
        assertSame(VALUE_2, ref.value)
    }

    private companion object {
        private const val VALUE_1 = "a"
        private const val VALUE_2 = "b"
        private const val VALUE_3 = "c"
    }
}
