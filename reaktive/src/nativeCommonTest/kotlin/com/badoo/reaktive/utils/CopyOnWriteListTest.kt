package com.badoo.reaktive.utils

import kotlin.native.concurrent.freeze
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CopyOnWriteListTest {

    private val list = CopyOnWriteList<Int?>().freeze()

    @Test
    fun contains() {
        add(0, null, 1, null, 2)

        assertTrue(list.contains(null))
        assertTrue(list.contains(1))
        assertFalse(list.contains(3))
    }

    @Test
    fun containsAll() {
        add(0, null, 1, null, 2)

        assertTrue(list.containsAll(listOf(null)))
        assertTrue(list.containsAll(listOf(null, null)))
        assertTrue(list.containsAll(listOf(null, null, null)))
        assertTrue(list.containsAll(listOf(1)))
        assertTrue(list.containsAll(listOf(1, 1)))
        assertTrue(list.containsAll(listOf(null, 0, 2)))
        assertFalse(list.containsAll(listOf(null, 0, 2, 3)))
    }

    @Test
    fun get() {
        add(0, null, 1, null, 2)

        assertEquals(null, list[1])
        assertEquals(1, list[2])
        assertFailsWith<IndexOutOfBoundsException> { list[5] }
        assertFailsWith<IndexOutOfBoundsException> { list[-1] }
    }

    @Test
    fun indexOf() {
        add(0, null, 1, 2)

        assertEquals(1, list.indexOf(null))
        assertEquals(2, list.indexOf(1))
        assertEquals(-1, list.indexOf(3))
    }

    @Test
    fun isEmpty() {
        assertTrue(list.isEmpty())
        list.add(0)
        assertFalse(list.isEmpty())
    }

    @Test
    fun lastIndexOf() {
        add(0, null, 1)

        assertEquals(2, list.lastIndexOf(1))
        assertEquals(-1, list.lastIndexOf(2))
    }

    @Test
    fun add() {
        list.add(null)
        list.add(0)
        list.add(1)
        list.add(2)
        list.add(0, 3)
        list.add(2, 4)
        list.add(5, 5)
        list.add(7, 6)

        assertEquals(listOf<Int?>(3, null, 4, 0, 1, 5, 2, 6), list)
    }

    @Test
    fun addAll() {
        val collection = List(5) { it }
        list.addAll(collection)
        list.addAll(0, collection)
        list.addAll(7, collection)
        list.addAll(15, collection)

        assertEquals(listOf<Int?>(0, 1, 2, 3, 4, 0, 1, 0, 1, 2, 3, 4, 2, 3, 4, 0, 1, 2, 3, 4), list)
    }

    @Test
    fun clear() {
        add(0, null, 1)

        list.clear()

        assertEquals(0, list.size)
        assertFalse(list.contains(0))
        assertFalse(list.contains(null))
        assertFalse(list.contains(1))
    }

    @Test
    fun remove() {
        add(0, null, 1)

        list.remove(null)
        assertEquals(2, list.size)
        assertFalse(list.contains(null))
        list.remove(0)
        assertEquals(1, list.size)
        assertFalse(list.contains(0))
        assertTrue(list.contains(1))
    }

    @Test
    fun removeAll() {
        add(0, null, 1, null, 1, 2)

        list.removeAll(listOf(null, 1))

        assertEquals(2, list.size)
        assertFalse(list.contains(null))
        assertFalse(list.contains(1))
        assertTrue(list.contains(0))
        assertTrue(list.contains(2))
    }

    @Test
    fun removeAt() {
        add(0, null, 1)

        list.removeAt(1)
        list.removeAt(1)

        assertFalse(list.contains(null))
        assertFalse(list.contains(1))
        assertTrue(list.contains(0))
    }

    @Test
    fun retainAll() {
        add(0, null, 1, null, 2)

        list.retainAll(listOf(null, 1))

        assertEquals(3, list.size)
        assertFalse(list.contains(0))
        assertTrue(list.contains(null))
        assertTrue(list.contains(1))
        assertFalse(list.contains(2))
    }

    @Test
    fun set() {
        add(0, null, 1)

        list[0] = 3
        list[1] = 4

        assertEquals(3, list.size)
        assertEquals(3, list[0])
        assertEquals(4, list[1])
        assertEquals(1, list[2])
    }

    private fun add(vararg items: Int?) {
        list.addAll(items)
    }
}
