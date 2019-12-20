package com.badoo.reaktive.observable

import kotlin.test.Test
import kotlin.test.assertEquals


class DistinctUntilChangedTest
    : ObservableToObservableTests by ObservableToObservableTestsImpl({ distinctUntilChanged() }) {

    private val thirteen = Question(13)
    private val fortyTwo = Question(42)

    @Test
    fun filter_numbers() {
        val actual = observableOf(1, 1, 2, 2, 3, 3)
            .distinctUntilChanged()
            .record()

        assertEquals(listOf(1, 2, 3), actual)
    }

    @Test
    fun filter_numbers_and_null() {
        val actual = observableOf(1, 1, null, null, 3, 3)
            .distinctUntilChanged()
            .record()

        assertEquals(listOf(1, null, 3), actual)
    }

    @Test
    fun filter_based_on_a_key_selector() {
        val observable = observableOf(
            NotEqualsValue(1),
            NotEqualsValue(1),
            NotEqualsValue(2),
            NotEqualsValue(2)
        )

        observable
            .distinctUntilChanged(keySelector = { it.value })
            .record()
            .map { it.value }
            .let { actual -> assertEquals(listOf(1, 2), actual) }

        observable
            .distinctUntilChanged()
            .record()
            .map { it.value }
            .let { actual -> assertEquals(listOf(1, 1, 2, 2), actual) }
    }

    @Test
    fun checks_whether_the_emissions_are_not_the_same_instance() {
        val actual = observableOf(thirteen, thirteen, fortyTwo, fortyTwo)
            .distinctUntilChanged(comparator = { l, r -> l === r })
            .record()

        assertEquals(listOf(thirteen, fortyTwo), actual)
    }

    private fun <T> Observable<T>.record(): List<T> =
        mutableListOf<T>().apply {
            subscribe { value ->
                add(value)
            }
        }

    @Suppress("EqualsOrHashCode")
    private data class NotEqualsValue<T>(val value: T) {
        override fun equals(other: Any?): Boolean = false
    }

    private data class Question(val answer: Int)
}
