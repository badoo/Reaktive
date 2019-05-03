package com.badoo.reaktive.observable

import kotlin.test.Test
import kotlin.test.assertEquals

class ScanTests {

    @Test
    fun scan() {
        val actual = observableOf(1, 2, 3)
            .scan { acc, value -> acc + value }
            .record()

        assertEquals(listOf(1, 3, 6), actual)
    }

    @Test
    fun `scan with seed`() {
        val actual = observableOf(2, 3)
            .scan(1) { acc: Int, value: Int -> acc + value }
            .record()

        assertEquals(listOf(1, 3, 6), actual)
    }

    private fun <T> Observable<T>.record(): List<T> =
        mutableListOf<T>().apply {
            subscribe { value ->
                add(value)
            }
        }

}
