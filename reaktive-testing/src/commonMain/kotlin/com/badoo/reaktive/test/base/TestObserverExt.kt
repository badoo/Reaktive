package com.badoo.reaktive.test.base

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

fun TestObserver.assertError() {
    assertTrue(isError, "Source did not fail")
}

fun TestObserver.assertError(expectedError: Throwable) {
    assertEquals(expectedError, error, "Source error does not match")
}

fun TestObserver.assertError(predicate: (Throwable) -> Boolean) {
    assertError()
    val error = this.error!!
    assertTrue(predicate(error), "Source error does not match the predicate: $error")
}

fun TestObserver.assertNotError() {
    assertFalse(isError, "Source failed")
}

fun TestObserver.assertSubscribed() {
    assertNotNull(disposable, "Source is not subscribed")
}

fun TestObserver.assertNotSubscribed() {
    assertNull(disposable, "Source is subscribed")
}

fun TestObserver.assertDisposed() {
    assertTrue(isDisposed, "Source is not disposed")
}

fun TestObserver.assertNotDisposed() {
    assertFalse(isDisposed, "Source is disposed")
}
