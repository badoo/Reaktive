package com.badoo.reaktive.utils.atomic

import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AtomicReferenceNativeTest {

    @Test
    fun accepts_not_frozen_initial_value() {
        AtomicReference(Any())
    }

    @Test
    fun does_not_freeze_initial_value() {
        val ref = AtomicReference(Any())

        assertFalse(ref.value.isFrozen)
    }

    @Test
    fun freezes_current_value_WHEN_frozen() {
        val ref = AtomicReference(Any())

        ref.freeze()

        assertTrue(ref.value.isFrozen)
    }

    @Test
    fun does_not_freeze_new_value_IF_not_frozen() {
        val ref = AtomicReference(Any())

        ref.value = Any()

        assertFalse(ref.value.isFrozen)
    }

    @Test
    fun freezes_new_value_IF_frozen() {
        val ref = AtomicReference(Any())

        ref.freeze()
        ref.value = Any()

        assertTrue(ref.value.isFrozen)
    }
}
