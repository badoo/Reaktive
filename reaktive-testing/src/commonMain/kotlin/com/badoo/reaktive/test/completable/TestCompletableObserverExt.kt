package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.utils.freeze
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun TestCompletableObserver.assertComplete(): TestCompletableObserver {
    assertTrue(isComplete, "Completable did not complete")

    return this
}

fun TestCompletableObserver.assertNotComplete(): TestCompletableObserver {
    assertFalse(isComplete, "Completable completed")

    return this
}

fun Completable.test(autoFreeze: Boolean = true): TestCompletableObserver {
    if (autoFreeze) {
        freeze()
    }

    return TestCompletableObserver(autoFreeze = autoFreeze)
        .also(::subscribe)
}
