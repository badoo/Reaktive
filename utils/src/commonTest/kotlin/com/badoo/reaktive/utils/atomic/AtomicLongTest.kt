package com.badoo.reaktive.utils.atomic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AtomicLongTest {

    @Test
    fun default_initial_value_is_0() {
        assertEquals(0L, AtomicLong().value)
    }

    @Test
    fun addAndGet_MAX_VALUE_minus_one() {
        val ref = AtomicLong(Long.MAX_VALUE)
        ref.addAndGet(-1L)
        assertEquals(Long.MAX_VALUE - 1L, ref.value)
    }

    @Test
    fun addAndGet_MAX_VALUE_plus_one() {
        val ref = AtomicLong(Long.MAX_VALUE)
        ref.addAndGet(1L)
        assertEquals(Long.MIN_VALUE, ref.value)
    }

    @Test
    fun addAndGet_MIN_VALUE_minus_one() {
        val ref = AtomicLong(Long.MIN_VALUE)
        ref.addAndGet(-1L)
        assertEquals(Long.MAX_VALUE, ref.value)
    }

    @Test
    fun addAndGet_MIN_VALUE_plus_one() {
        val ref = AtomicLong(Long.MIN_VALUE)
        ref.addAndGet(1L)
        assertEquals(Long.MIN_VALUE + 1L, ref.value)
    }

    @Test
    fun compareAndSet_success_from_MAX_VALUE_to_0() {
        val ref = AtomicLong(Long.MAX_VALUE)
        val result = ref.compareAndSet(Long.MAX_VALUE, 0L)
        assertTrue(result)
        assertEquals(0L, ref.value)
    }

    @Test
    fun compareAndSet_fail_from_MAX_VALUE_to_0() {
        val ref = AtomicLong(Long.MAX_VALUE)
        val result = ref.compareAndSet(0L, 0L)
        assertFalse(result)
        assertEquals(Long.MAX_VALUE, ref.value)
    }

    @Test
    fun compareAndSet_success_from_MIN_VALUE_to_0() {
        val ref = AtomicLong(Long.MIN_VALUE)
        val result = ref.compareAndSet(Long.MIN_VALUE, 0L)
        assertTrue(result)
        assertEquals(0L, ref.value)
    }

    @Test
    fun compareAndSet_fail_from_MIN_VALUE_to_0() {
        val ref = AtomicLong(Long.MIN_VALUE)
        val result = ref.compareAndSet(0L, 0L)
        assertFalse(result)
        assertEquals(Long.MIN_VALUE, ref.value)
    }
}
