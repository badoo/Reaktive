package com.badoo.reaktive.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class PlusSortedTest {

    private val comparator = compareBy<Int> { it }

    @Test
    fun inserts_item_into_beginning() {
        val list = listOf(1, 2, 3, 4, 5).plusSorted(0, comparator)

        assertEquals(listOf(0, 1, 2, 3, 4, 5), list)
    }

    @Test
    fun inserts_item_into_end() {
        val list = listOf(1, 2, 3, 4, 5).plusSorted(6, comparator)

        assertEquals(listOf(1, 2, 3, 4, 5, 6), list)
    }

    @Test
    fun inserts_item_into_middle() {
        val list = listOf(1, 2, 4, 5, 6).plusSorted(3, comparator)

        assertEquals(listOf(1, 2, 3, 4, 5, 6), list)
    }

    @Test
    fun inserts_duplicate_item_into_beginning() {
        val list = listOf(1, 2, 3, 4, 5).plusSorted(1, comparator)

        assertEquals(listOf(1, 1, 2, 3, 4, 5), list)
    }

    @Test
    fun inserts_duplicate_item_into_end() {
        val list = listOf(1, 2, 3, 4, 5).plusSorted(5, comparator)

        assertEquals(listOf(1, 2, 3, 4, 5, 5), list)
    }

    @Test
    fun inserts_duplicate_item_into_middle() {
        val list = listOf(1, 2, 3, 4, 5).plusSorted(3, comparator)

        assertEquals(listOf(1, 2, 3, 3, 4, 5), list)
    }

    @Test
    fun inserts_into_empty_list() {
        val list = emptyList<Int>().plusSorted(0, comparator)

        assertEquals(listOf(0), list)
    }
}
