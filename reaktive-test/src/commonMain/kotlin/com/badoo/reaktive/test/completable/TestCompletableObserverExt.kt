package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.test.utils.assertFalse
import com.badoo.reaktive.test.utils.assertTrue

fun TestCompletableObserver.assertComplete() {
    assertTrue(isComplete, "Completable did not complete")
}

fun TestCompletableObserver.assertNotComplete() {
    assertFalse(isComplete, "Completable is complete")
}

fun Completable.test(): TestCompletableObserver =
    TestCompletableObserver()
        .also(::subscribe)