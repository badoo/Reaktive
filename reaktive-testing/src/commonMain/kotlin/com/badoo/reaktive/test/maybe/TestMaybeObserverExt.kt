package com.badoo.reaktive.test.maybe

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.utils.freeze
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun <T> TestMaybeObserver<T>.assertSuccess() {
    assertTrue(isSuccess, "Maybe did not succeed")
}

fun <T> TestMaybeObserver<T>.assertSuccess(expectedValue: T) {
    assertSuccess()
    assertEquals(expectedValue, value, "Maybe success value does not match")
}

fun <T> TestMaybeObserver<T>.assertNotSuccess() {
    assertFalse(isSuccess, "Maybe succeeded")
}

fun TestMaybeObserver<*>.assertComplete() {
    assertTrue(isComplete, "Maybe did not complete")
}

fun TestMaybeObserver<*>.assertNotComplete() {
    assertFalse(isComplete, "Maybe completed")
}

fun <T> Maybe<T>.test(autoFreeze: Boolean = true): TestMaybeObserver<T> {
    if (autoFreeze) {
        freeze()
    }

    return TestMaybeObserver<T>(autoFreeze = autoFreeze)
        .also(::subscribe)
}
