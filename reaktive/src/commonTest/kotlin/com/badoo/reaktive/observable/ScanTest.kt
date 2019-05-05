package com.badoo.reaktive.observable

import kotlin.test.Test
import kotlin.test.assertEquals

class ScanTest {

    @Test
    fun sum_numbers() {
        val actual = observableOf(1, 2, 3)
            .scan { acc, value -> acc + value }
            .record()

        assertEquals(listOf(1, 3, 6), actual)
    }

    @Test
    fun sum_numbers_with_null() {
        val actual = observableOf(1, 2, null, 3)
            .scan { acc, value -> (acc ?: 0) + (value ?: 0) }
            .record()

        assertEquals(listOf(1, 3, 3, 6), actual)
    }

    @Test
    fun sum_numbers_with_seed() {
        val actual = observableOf(2, 3)
            .scan(1) { acc, value -> acc + value }
            .record()

        assertEquals(listOf(1, 3, 6), actual)
    }

    @Test
    fun sum_numbers_with_seed_that_contains_null() {
        val actual = observableOf(2, null, 3)
            .scan(1) { acc, value -> acc + (value ?: 0) }
            .record()

        assertEquals(listOf(1, 3, 3, 6), actual)
    }

    private fun <T> Observable<T>.record(): List<T> =
        mutableListOf<T>().apply {
            subscribe { value ->
                add(value)
            }
        }

}
