package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.test.utils.assertEquals
import com.badoo.reaktive.test.utils.assertFalse
import com.badoo.reaktive.test.utils.assertTrue

fun <T> TestSingleObserver<T>.assertSuccess() {
    assertTrue(isSuccess, "Single was not success")
}

fun <T> TestSingleObserver<T>.assertSuccess(expectedValue: T) {
    assertEquals(expectedValue, value, "Single success values do not match")
}

fun <T> TestSingleObserver<T>.assertNotSuccess() {
    assertFalse(isSuccess, "Single is succeeded")
}

fun <T> Single<T>.test(): TestSingleObserver<T> =
    TestSingleObserver<T>()
        .also(::subscribe)