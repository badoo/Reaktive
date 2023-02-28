package com.badoo.reaktive.utils

import kotlin.native.concurrent.isFrozen
import kotlin.test.Test
import kotlin.test.assertFalse

class ObjectReferenceNativeTest {

    @Test
    fun accepts_not_frozen_initial_value() {
        ObjectReference(Any())
    }

    @Test
    fun does_not_freeze_initial_value() {
        val ref = ObjectReference(Any())

        assertFalse(ref.value.isFrozen)
    }

    @Test
    fun does_not_freeze_new_value_IF_not_frozen() {
        val ref = ObjectReference(Any())

        ref.value = Any()

        assertFalse(ref.value.isFrozen)
    }
}
