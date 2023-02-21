package com.badoo.reaktive.test.assert

import kotlin.test.Test
import kotlin.test.assertFailsWith as assertFailsWithKt

class AssertTest {

    @Test
    fun fail_fails() {
        assertFailsWithKt<AssertionError> { fail() }
    }

    @Test
    fun assertTrue_fails_WHEN_false() {
        assertFailsWithKt<AssertionError> { assertTrue(false) }
    }

    @Test
    fun assertTrue_succeeds_WHEN_true() {
        assertTrue(true)
    }

    @Test
    fun assertFalse_fails_WHEN_true() {
        assertFailsWithKt<AssertionError> { assertFalse(true) }
    }

    @Test
    fun assertFalse_succeeds_WHEN_false() {
        assertFalse(false)
    }

    @Test
    fun assertEquals_fails_WHEN_not_equal() {
        assertFailsWithKt<AssertionError> { assertEquals(1, 2) }
    }

    @Test
    fun assertEquals_succeeds_WHEN_equal() {
        assertEquals(1, 1)
    }

    @Test
    fun assertNull_fails_WHEN_not_null() {
        assertFailsWithKt<AssertionError> { assertNull(1) }
    }

    @Test
    fun assertNull_succeeds_WHEN_null() {
        assertNull(null)
    }

    @Test
    fun assertNotNull_fails_WHEN_null() {
        assertFailsWithKt<AssertionError> { assertNotNull(null) }
    }

    @Test
    fun assertNotNull_succeeds_WHEN_not_null() {
        assertNotNull(1)
    }
}
