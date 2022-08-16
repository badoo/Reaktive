package com.badoo.reaktive.test.assert

internal fun fail(message: String? = null) {
    throw AssertionError(message)
}

internal fun assertTrue(actual: Boolean, message: String? = null) {
    if (!actual) {
        fail(message ?: "Expected value to be true")
    }
}

internal fun assertFalse(actual: Boolean, message: String? = null) {
    assertTrue(!actual, message ?: "Expected value to be false")
}

internal fun <T> assertEquals(expected: T, actual: T, message: String? = null) {
    assertTrue(expected == actual, formatMessage(first = message, second = "Expected <$expected>, actual <$actual>"))
}

internal fun assertNull(actual: Any?, message: String? = null) {
    assertTrue(actual == null, formatMessage(first = message, second = "Expected null, actual <$actual>"))
}

internal fun assertNotNull(actual: Any?, message: String? = null) {
    assertTrue(actual != null, formatMessage(first = message, second = "Expected value to be not null"))
}

private fun formatMessage(first: String?, second: String): String =
    if (first == null) second else "$first. $second."
