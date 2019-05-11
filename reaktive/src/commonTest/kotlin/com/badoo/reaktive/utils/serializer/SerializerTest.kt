package com.badoo.reaktive.utils.serializer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SerializerTest {

    private val values = arrayListOf<Int>()
    private val comparator = Comparator<Int>(Int::compareTo)

    @Test
    fun value_emitted_WHEN_sent() {
        val serializer =
            object : Serializer<Int>() {
                override fun onValue(value: Int): Boolean {
                    values += value
                    return true
                }
            }

        serializer.accept(1)

        assertValues(1)
    }

    @Test
    fun all_values_emitted_in_the_same_order_WHEN_sent_sequentially() {
        val serializer =
            object : Serializer<Int>() {
                override fun onValue(value: Int): Boolean {
                    values += value
                    return true
                }
            }

        serializer.accept(1)
        serializer.accept(2)
        serializer.accept(3)

        assertValues(1, 2, 3)
    }

    @Test
    fun comparator_does_not_affect_WHEN_values_sent_sequentially() {
        val serializer =
            object : Serializer<Int>(comparator) {
                override fun onValue(value: Int): Boolean {
                    values += value
                    return true
                }
            }

        serializer.accept(3)
        serializer.accept(2)
        serializer.accept(1)

        assertValues(3, 2, 1)
    }

    @Test
    fun onValue_not_called_recursively_WHEN_value_sent_from_callback() {
        var count = 0
        var success = false

        val serializer =
            object : Serializer<Int>() {
                override fun onValue(value: Int): Boolean {
                    count++
                    if (value == 1) {
                        accept(2)
                        success = count == 1
                    }

                    return true
                }
            }

        serializer.accept(1)

        assertTrue(success)
    }

    @Test
    fun both_values_are_emitted_WHEN_second_value_sent_from_callback() {
        val serializer =
            object : Serializer<Int>() {
                override fun onValue(value: Int): Boolean {
                    values += value
                    if (value == 1) {
                        accept(2)
                    }

                    return true
                }
            }

        serializer.accept(1)

        assertValues(1, 2)
    }

    @Test
    fun only_first_value_is_emitted_WHEN_more_values_sent_from_callback_and_false_returned() {
        val serializer =
            object : Serializer<Int>() {
                override fun onValue(value: Int): Boolean {
                    values += value

                    return if (value == 1) {
                        accept(2)
                        accept(3)
                        false
                    } else {
                        true
                    }
                }
            }

        serializer.accept(1)

        assertValues(1)
    }

    @Test
    fun values_sent_from_callback_are_emitted_sorted_WHEN_has_comparator() {
        val serializer =
            object : Serializer<Int>(comparator) {
                override fun onValue(value: Int): Boolean {
                    values += value
                    if (value == 3) {
                        accept(4)
                        accept(2)
                        accept(5)
                        accept(1)
                    }
                    return true
                }
            }

        serializer.accept(3)

        assertValues(3, 1, 2, 4, 5)
    }

    @Test
    fun remaining_values_are_not_emitted_WHEN_cleared() {
        var count = 0

        val serializer =
            object : Serializer<Int>() {
                override fun onValue(value: Int): Boolean {
                    count++
                    if (value == 1) {
                        accept(2)
                        clear()
                    }

                    return true
                }
            }

        serializer.accept(1)

        assertEquals(1, count)
    }

    private fun assertValues(vararg values: Int) {
        assertEquals(values.toList(), this.values)
    }
}