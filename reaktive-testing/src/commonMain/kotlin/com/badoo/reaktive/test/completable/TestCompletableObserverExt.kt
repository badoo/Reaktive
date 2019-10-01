package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.utils.freeze
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun TestCompletableObserver.assertComplete() {
    assertTrue(isComplete, "Completable did not complete")
}

fun TestCompletableObserver.assertNotComplete() {
    assertFalse(isComplete, "Completable is complete")
}

fun Completable.test(): TestCompletableObserver {
    freeze()

    return TestCompletableObserver()
        .also(::subscribe)
}