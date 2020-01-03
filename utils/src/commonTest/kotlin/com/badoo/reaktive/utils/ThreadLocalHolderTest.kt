package com.badoo.reaktive.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ThreadLocalHolderTest {

    @Test
    fun value_is_null_WHEN_created_without_initial_value() {
        val storage = ThreadLocalHolder<Unit>()

        assertNull(storage.get())
    }

    @Test
    fun value_is_same_as_initial_WHEN_created_with_initial_value() {
        val storage = ThreadLocalHolder(Unit)

        assertSame(Unit, storage.get())
    }

    @Test
    fun value_is_new_WHEN_new_value_is_set() {
        val storage = ThreadLocalHolder(0)
        storage.set(1)

        assertEquals(1, storage.get())
    }

    @Test
    fun value_is_null_WHEN_had_value_and_disposed() {
        val storage = ThreadLocalHolder(Unit)
        storage.dispose()

        assertNull(storage.get())
    }

    @Test
    fun throws_IllegalStateException_WHEN_disposed_and_set_value() {
        val storage = ThreadLocalHolder<Unit>()
        storage.dispose()

        assertFailsWith<IllegalStateException> {
            storage.set(Unit)
        }
    }

    @Test
    fun isDisposed_is_false_WHEN_not_disposed() {
        assertFalse(ThreadLocalHolder<Unit>().isDisposed)
    }

    @Test
    fun isDisposed_is_true_WHEN_disposed() {
        val storage = ThreadLocalHolder<Unit>()
        storage.dispose()

        assertTrue(storage.isDisposed)
    }
}
