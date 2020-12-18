package com.badoo.reaktive.test.base

import com.badoo.reaktive.utils.printStack
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

fun <T : TestObserver> T.assertError(): T {
    assertTrue(isError, "Source did not fail")

    return this
}

fun <T : TestObserver> T.assertError(expectedError: Throwable): T {
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

    return this
}

fun <T : TestObserver> T.assertError(predicate: (Throwable) -> Boolean): T {
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

    return this
}

fun <T : TestObserver> T.assertNotError(): T {
    error?.also {
        it.printStack()
        fail("Source failed, the actual error is printed above")
    }

    return this
}

fun <T : TestObserver> T.assertSubscribed(): T {
    assertNotNull(disposable, "Source is not subscribed")

    return this
}

fun <T : TestObserver> T.assertNotSubscribed(): T {
    assertNull(disposable, "Source is subscribed")

    return this
}

fun <T : TestObserver> T.assertDisposed(): T {
    assertTrue(isDisposed, "Source is not disposed")

    return this
}

fun <T : TestObserver> T.assertNotDisposed(): T {
    assertFalse(isDisposed, "Source is disposed")

    return this
}
