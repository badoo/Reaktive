package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.test.assert.assertFalse
import com.badoo.reaktive.test.assert.assertTrue

fun TestCompletableObserver.assertComplete(): TestCompletableObserver {
    assertTrue(isComplete, "Completable did not complete")

    return this
}

fun TestCompletableObserver.assertNotComplete(): TestCompletableObserver {
    assertFalse(isComplete, "Completable completed")

    return this
}

fun Completable.test(): TestCompletableObserver =
    TestCompletableObserver()
        .also(::subscribe)
