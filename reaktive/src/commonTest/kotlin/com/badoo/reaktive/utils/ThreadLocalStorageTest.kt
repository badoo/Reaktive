package com.badoo.reaktive.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ThreadLocalStorageTest {

    @Test
    fun value_is_null_WHEN_created_without_initial_value() {
        val storage = ThreadLocalStorage<Unit>()

        assertNull(storage.value)
    }

    @Test
    fun value_is_same_as_initial_WHEN_created_with_initial_value() {
        val storage = ThreadLocalStorage(Unit)

        assertSame(Unit, storage.value)
    }

    @Test
    fun value_is_new_WHEN_new_value_is_set() {
        val storage = ThreadLocalStorage(0)
        storage.set(1)

        assertEquals(1, storage.value)
    }

    @Test
    fun value_is_null_WHEN_had_value_and_disposed() {
        val storage = ThreadLocalStorage(Unit)
        storage.dispose()

        assertNull(storage.value)
    }

    @Test
    fun throws_IllegalStateException_WHEN_disposed_and_set_value() {
        val storage = ThreadLocalStorage<Unit>()
        storage.dispose()

        assertFailsWith<IllegalStateException> {
            storage.set(Unit)
        }
    }

    @Test
    fun isDisposed_is_false_WHEN_not_disposed() {
        assertFalse(ThreadLocalStorage<Unit>().isDisposed)
    }

    @Test
    fun isDisposed_is_true_WHEN_disposed() {
        val storage = ThreadLocalStorage<Unit>()
        storage.dispose()

        assertTrue(storage.isDisposed)
    }
}