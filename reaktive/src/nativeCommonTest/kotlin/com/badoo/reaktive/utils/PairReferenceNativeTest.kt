package com.badoo.reaktive.utils

import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PairReferenceNativeTest {

    @Test
    fun accepts_not_frozen_initial_values() {
        PairReference(Any(), Any())
    }

    @Test
    fun does_not_freeze_initial_values() {
        val ref = PairReference(Any(), Any())

        assertFalse(ref.first.isFrozen)
        assertFalse(ref.second.isFrozen)
    }

    @Test
    fun freezes_current_values_WHEN_frozen() {
        val ref = PairReference(Any(), Any())

        ref.freeze()

        assertTrue(ref.first.isFrozen)
        assertTrue(ref.second.isFrozen)
    }

    @Test
    fun does_not_freeze_new_values_IF_not_frozen() {
        val ref = PairReference(Any(), Any())

        ref.first = Any()
        ref.second = Any()

        assertFalse(ref.first.isFrozen)
        assertFalse(ref.second.isFrozen)
    }

    @Test
    fun freezes_new_values_IF_frozen() {
        val ref = PairReference(Any(), Any())

        ref.freeze()
        ref.first = Any()
        ref.second = Any()

        assertTrue(ref.first.isFrozen)
        assertTrue(ref.second.isFrozen)
    }
}
