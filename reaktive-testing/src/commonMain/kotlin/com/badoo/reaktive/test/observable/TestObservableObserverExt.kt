package com.badoo.reaktive.test.observable

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.utils.freeze
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun <T> TestObservableObserver<T>.assertValues(expectedValues: List<T>) {
    assertEquals(expectedValues, values, "Observable values do not match")
}

fun <T> TestObservableObserver<T>.assertValues(vararg expectedValues: T) {
    assertValues(listOf(*expectedValues))
}

fun <T> TestObservableObserver<T>.assertValue(expectedValue: T) {
    assertValues(listOf(expectedValue))
}

fun TestObservableObserver<*>.assertNoValues() {
    assertValues(emptyList())
}

fun TestObservableObserver<*>.assertComplete() {
    assertTrue(isComplete, "Observable did not complete")
}

fun TestObservableObserver<*>.assertNotComplete() {
    assertFalse(isComplete, "Observable completed")
}

fun <T> Observable<T>.test(autoFreeze: Boolean = true): TestObservableObserver<T> {
    if (autoFreeze) {
        freeze()
    }

    return TestObservableObserver<T>(autoFreeze = autoFreeze)
        .also(::subscribe)
}
