package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.utils.freeze
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun <T> TestSingleObserver<T>.assertSuccess() {
    assertTrue(isSuccess, "Single was not success")
}

fun <T> TestSingleObserver<T>.assertSuccess(expectedValue: T) {
    assertSuccess()
    assertEquals(expectedValue, value, "Single success values do not match")
}

fun <T> TestSingleObserver<T>.assertNotSuccess() {
    assertFalse(isSuccess, "Single is succeeded")
}

fun <T> Single<T>.test(autoFreeze: Boolean = true): TestSingleObserver<T> {
    if (autoFreeze) {
        freeze()
    }

    return TestSingleObserver<T>(autoFreeze = autoFreeze)
        .also(::subscribe)
}
