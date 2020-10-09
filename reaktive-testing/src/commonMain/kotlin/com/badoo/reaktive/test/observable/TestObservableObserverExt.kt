package com.badoo.reaktive.test.observable

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.utils.freeze
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun <T> TestObservableObserver<T>.assertValues(expectedValues: List<T>): TestObservableObserver<T> {
    assertEquals(expectedValues, values, "Observable values do not match")

    return this
}

fun <T> TestObservableObserver<T>.assertValues(vararg expectedValues: T): TestObservableObserver<T> {
    assertValues(listOf(*expectedValues))

    return this
}

fun <T> TestObservableObserver<T>.assertValue(expectedValue: T): TestObservableObserver<T> {
    assertValues(listOf(expectedValue))

    return this
}

fun <T> TestObservableObserver<T>.assertNoValues(): TestObservableObserver<T> {
    assertValues(emptyList())

    return this
}

fun <T> TestObservableObserver<T>.assertComplete(): TestObservableObserver<T> {
    assertTrue(isComplete, "Observable did not complete")

    return this
}

fun <T> TestObservableObserver<T>.assertNotComplete(): TestObservableObserver<T> {
    assertFalse(isComplete, "Observable completed")

    return this
}

fun <T> Observable<T>.test(autoFreeze: Boolean = true): TestObservableObserver<T> {
    if (autoFreeze) {
        freeze()
    }

    return TestObservableObserver<T>(autoFreeze = autoFreeze)
        .also(::subscribe)
}
