package com.badoo.reaktive.utils.atomic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AtomicIntTest {

    @Test
    fun default_initial_value_is_0() {
        assertEquals(0, AtomicInt().value)
    }

    @Test
    fun addAndGet_MAX_VALUE_minus_one() {
        val ref = AtomicInt(Int.MAX_VALUE)
        ref.addAndGet(-1)
        assertEquals(Int.MAX_VALUE - 1, ref.value)
    }

    @Test
    fun addAndGet_MAX_VALUE_plus_one() {
        val ref = AtomicInt(Int.MAX_VALUE)
        ref.addAndGet(1)
        assertEquals(Int.MIN_VALUE, ref.value)
    }


    @Test
    fun addAndGet_MIN_VALUE_minus_one() {
        val ref = AtomicInt(Int.MIN_VALUE)
        ref.addAndGet(-1)
        assertEquals(Int.MAX_VALUE, ref.value)
    }

    @Test
    fun addAndGet_MIN_VALUE_plus_one() {
        val ref = AtomicInt(Int.MIN_VALUE)
        ref.addAndGet(1)
        assertEquals(Int.MIN_VALUE + 1, ref.value)
    }

    @Test
    fun compareAndSet_success_from_MAX_VALUE_to_0() {
        val ref = AtomicInt(Int.MAX_VALUE)
        val result = ref.compareAndSet(Int.MAX_VALUE, 0)
        assertTrue(result)
        assertEquals(0, ref.value)
    }

    @Test
    fun compareAndSet_fail_from_MAX_VALUE_to_0() {
        val ref = AtomicInt(Int.MAX_VALUE)
        val result = ref.compareAndSet(0, 0)
        assertFalse(result)
        assertEquals(Int.MAX_VALUE, ref.value)
    }

    @Test
    fun compareAndSet_success_from_MIN_VALUE_to_0() {
        val ref = AtomicInt(Int.MIN_VALUE)
        val result = ref.compareAndSet(Int.MIN_VALUE, 0)
        assertTrue(result)
        assertEquals(0, ref.value)
    }

    @Test
    fun compareAndSet_fail_from_MIN_VALUE_to_0() {
        val ref = AtomicInt(Int.MIN_VALUE)
        val result = ref.compareAndSet(0, 0)
        assertFalse(result)
        assertEquals(Int.MIN_VALUE, ref.value)
    }
}
