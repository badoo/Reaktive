package com.badoo.reaktive.test.base

import com.badoo.reaktive.utils.printStack
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

fun TestObserver.assertError() {
    assertTrue(isError, "Source did not fail")
}

fun TestObserver.assertError(expectedError: Throwable) {
    val error = error
    if (error == null) {
        fail("Source did not fail")
    } else {
        try {
            assertEquals(expectedError, error, "Source error does not match, the actual error is printed above")
        } catch (e: AssertionError) {
            error.printStack()
            throw e
        }
    }
}

fun TestObserver.assertError(predicate: (Throwable) -> Boolean) {
    val error = error
    if (error == null) {
        fail("Source did not fail")
    } else {
        try {
            assertTrue(predicate(error), "Source error does not match the predicate, the actual error is printed above")
        } catch (e: AssertionError) {
            error.printStack()
            throw e
        }
    }
}

fun TestObserver.assertNotError() {
    error?.also {
        it.printStack()
        fail("Source failed, the actual error is printed above")
    }
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
