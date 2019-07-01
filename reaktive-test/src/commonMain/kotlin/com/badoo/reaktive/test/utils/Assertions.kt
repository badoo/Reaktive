/**
 * For some reason adding 'test-common' dependency to 'commonMain' source set does not compile.
 * We will use own assertions till that time.
 */

package com.badoo.reaktive.test.utils

internal fun assertTrue(condition: Boolean, message: String? = null) {
    if (!condition) {
        fail(message ?: "Expected value to be true.")
    }
}

internal fun assertFalse(condition: Boolean, message: String? = null) {
    if (condition) {
        fail(message ?: "Expected value to be false.")
    }
}

internal fun <T> assertEquals(expected: T, actual: T, message: String? = null) {
    assertTrue(expected == actual, prefix(message) + "Expected <$expected>, actual <$actual>.")
}

internal fun fail(message: String) {
    throw AssertionError(message)
}

private fun prefix(text: String?): String = text?.plus(". ") ?: ""

internal class AssertionError(message: String) : Error(message)