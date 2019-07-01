package com.badoo.reaktive.test.base

import com.badoo.reaktive.test.utils.assertEquals
import com.badoo.reaktive.test.utils.assertFalse
import com.badoo.reaktive.test.utils.assertTrue

val TestObserver.isError: Boolean get() = error != null

fun TestObserver.assertError() {
    assertTrue(isError)
}

fun TestObserver.assertError(expectedError: Throwable) {
    assertEquals(expectedError, error)
}

fun TestObserver.assertNotError() {
    assertFalse(isError)
}

fun TestObserver.assertSubscribed() {
    assertTrue(disposable != null)
}

fun TestObserver.assertDisposed() {
    assertTrue(isDisposed)
}

fun TestObserver.assertNotDisposed() {
    assertFalse(isDisposed)
}