package com.badoo.reaktive.utils.atomic

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AtomicBooleanTest {

    @Test
    fun default_initial_value_is_false() {
        assertFalse(AtomicBoolean().value)
    }

    @Test
    fun compareAndSet_success_from_false_to_true() {
        val ref = AtomicBoolean(false)
        val result = ref.compareAndSet(false, true)
        assertTrue(result)
        assertTrue(ref.value)
    }

    @Test
    fun compareAndSet_fail_from_false_to_true() {
        val ref = AtomicBoolean(false)
        val result = ref.compareAndSet(true, true)
        assertFalse(result)
        assertFalse(ref.value)
    }

    @Test
    fun compareAndSet_success_from_true_to_false() {
        val ref = AtomicBoolean(true)
        val result = ref.compareAndSet(true, false)
        assertTrue(result)
        assertFalse(ref.value)
    }

    @Test
    fun compareAndSet_fail_from_true_to_false() {
        val ref = AtomicBoolean(true)
        val result = ref.compareAndSet(false, false)
        assertFalse(result)
        assertTrue(ref.value)
    }
}
