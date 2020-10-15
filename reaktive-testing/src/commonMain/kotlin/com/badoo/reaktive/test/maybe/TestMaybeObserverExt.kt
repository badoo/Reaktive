package com.badoo.reaktive.test.maybe

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.utils.freeze
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun <T> TestMaybeObserver<T>.assertSuccess(): TestMaybeObserver<T> {
    assertTrue(isSuccess, "Maybe did not succeed")

    return this
}

fun <T> TestMaybeObserver<T>.assertSuccess(expectedValue: T): TestMaybeObserver<T> {
    assertSuccess()
    assertEquals(expectedValue, value, "Maybe success value does not match")

    return this
}

fun <T> TestMaybeObserver<T>.assertNotSuccess(): TestMaybeObserver<T> {
    assertFalse(isSuccess, "Maybe succeeded")

    return this
}

fun <T> TestMaybeObserver<T>.assertComplete(): TestMaybeObserver<T> {
    assertTrue(isComplete, "Maybe did not complete")

    return this
}

fun <T> TestMaybeObserver<T>.assertNotComplete(): TestMaybeObserver<T> {
    assertFalse(isComplete, "Maybe completed")

    return this
}

fun <T> Maybe<T>.test(autoFreeze: Boolean = true): TestMaybeObserver<T> {
    if (autoFreeze) {
        freeze()
    }

    return TestMaybeObserver<T>(autoFreeze = autoFreeze)
        .also(::subscribe)
}
