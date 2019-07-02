package com.badoo.reaktive.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionExtTest {

    private val list = listOf(0, 1, 2, 3, 4)

    @Test
    fun replace_first_item() {
        val newList = list.replace(0, -1)

        assertEquals(listOf(-1, 1, 2, 3, 4), newList)
    }

    @Test
    fun replace_middle_item() {
        val newList = list.replace(2, -1)

        assertEquals(listOf(0, 1, -1, 3, 4), newList)
    }

    @Test
    fun replace_last_item() {
        val newList = list.replace(4, -1)

        assertEquals(listOf(0, 1, 2, 3, -1), newList)
    }

    @Test
    fun insert_value_before_first_item() {
        val newList = list.insert(0, -1)

        assertEquals(listOf(-1, 0, 1, 2, 3, 4), newList)
    }

    @Test
    fun insert_value_to_the_middle() {
        val newList = list.insert(2, -1)

        assertEquals(listOf(0, 1, -1, 2, 3, 4), newList)
    }

    @Test
    fun insert_value_before_last_item() {
        val newList = list.insert(4, -1)

        assertEquals(listOf(0, 1, 2, 3, -1, 4), newList)
    }

    @Test
    fun insert_value_after_last_item() {
        val newList = list.insert(5, -1)

        assertEquals(listOf(0, 1, 2, 3, 4, -1), newList)
    }
}