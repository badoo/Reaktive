package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.test.assert.assertEquals
import com.badoo.reaktive.test.assert.assertFalse
import com.badoo.reaktive.test.assert.assertTrue

fun <T> TestSingleObserver<T>.assertSuccess(): TestSingleObserver<T> {
    assertTrue(isSuccess, "Single did not succeed")

    return this
}

fun <T> TestSingleObserver<T>.assertSuccess(expectedValue: T): TestSingleObserver<T> {
    assertSuccess()
    assertEquals(expectedValue, value, "Single success value does not match")

    return this
}

fun <T> TestSingleObserver<T>.assertNotSuccess(): TestSingleObserver<T> {
    assertFalse(isSuccess, "Single succeeded")

    return this
}

fun <T> Single<T>.test(): TestSingleObserver<T> =
    TestSingleObserver<T>()
        .also(::subscribe)
