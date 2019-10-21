package com.badoo.reaktive.test.base

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

val TestObserver.isError: Boolean get() = error != null

fun TestObserver.assertError() {
    assertTrue(isError)
}

fun TestObserver.assertError(expectedError: Throwable) {
    assertEquals(expectedError, error)
}

fun TestObserver.assertError(predicate: (Throwable) -> Boolean) {
    val error = this.error
    assertNotNull(error)
    assertTrue(predicate(error))
}

fun TestObserver.assertNotError() {
    assertFalse(isError)
}

fun TestObserver.assertSubscribed() {
    assertNotNull(disposable)
}

fun TestObserver.assertNotSubscribed() {
    assertNull(disposable)
}

fun TestObserver.assertDisposed() {
    assertTrue(isDisposed)
}

fun TestObserver.assertNotDisposed() {
    assertFalse(isDisposed)
}
