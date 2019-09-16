package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun TestCompletableObserver.assertComplete() {
    assertTrue(isComplete, "Completable did not complete")
}

fun TestCompletableObserver.assertNotComplete() {
    assertFalse(isComplete, "Completable is complete")
}

fun TestCompletableObserver.disposeIfTerminalEvent() {
    if (isComplete || error != null) {
        dispose()
    }
}

fun Completable.test(): TestCompletableObserver =
    TestCompletableObserver()
        .also(::subscribe)