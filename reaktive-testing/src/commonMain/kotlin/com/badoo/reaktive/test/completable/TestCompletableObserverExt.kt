package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.utils.freeze
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun TestCompletableObserver.assertComplete() {
    assertTrue(isComplete, "Completable did not complete")
}

fun TestCompletableObserver.assertNotComplete() {
    assertFalse(isComplete, "Completable completed")
}

fun Completable.test(autoFreeze: Boolean = true): TestCompletableObserver {
    if (autoFreeze) {
        freeze()
    }

    return TestCompletableObserver(autoFreeze = autoFreeze)
        .also(::subscribe)
}
