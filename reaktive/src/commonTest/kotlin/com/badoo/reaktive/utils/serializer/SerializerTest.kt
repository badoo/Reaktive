package com.badoo.reaktive.utils.serializer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SerializerTest {

    private val values = arrayListOf<Int?>()
    private val comparator = Comparator<Int> { a, b -> a.compareTo(b) }

    @Test
    fun value_emitted_WHEN_sent() {
        val serializer =
            serializer<Int> {
                values += it
                true
            }

        serializer.accept(1)

        assertValues(1)
    }

    @Test
    fun all_values_emitted_in_the_same_order_WHEN_sent_sequentially() {
        val serializer =
            serializer<Int?> {
                values += it
                true
            }

        serializer.accept(1)
        serializer.accept(null)
        serializer.accept(3)

        assertValues(1, null, 3)
    }

    @Test
    fun comparator_does_not_affect_WHEN_values_sent_sequentially() {
        val serializer =
            serializer(comparator) {
                values += it
                true
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

        lateinit var serializer: Serializer<Int>
        serializer =
            serializer {
                count++
                if (it == 1) {
                    serializer.accept(2)
                    success = count == 1
                }
                true
            }

        serializer.accept(1)

        assertTrue(success)
    }

    @Test
    fun all_values_are_emitted_WHEN_some_are_sent_from_callback() {
        lateinit var serializer: Serializer<Int?>
        serializer =
            serializer {
                values += it
                if (it == 1) {
                    serializer.accept(null)
                    serializer.accept(2)
                }
                true
            }

        serializer.accept(1)

        assertValues(1, null, 2)
    }

    @Test
    fun only_first_value_is_emitted_WHEN_more_values_sent_from_callback_and_false_returned() {
        lateinit var serializer: Serializer<Int>
        serializer =
            serializer { value ->
                values += value

                if (value == 1) {
                    serializer.accept(2)
                    serializer.accept(3)
                    false
                } else {
                    true
                }
            }

        serializer.accept(1)

        assertValues(1)
    }

    @Test
    fun values_sent_from_callback_are_emitted_sorted_WHEN_has_comparator() {
        lateinit var serializer: Serializer<Int>
        serializer =
            serializer(comparator) { value ->
                values += value
                if (value == 3) {
                    serializer.accept(4)
                    serializer.accept(2)
                    serializer.accept(5)
                    serializer.accept(1)
                }
                true
            }

        serializer.accept(3)

        assertValues(3, 1, 2, 4, 5)
    }

    @Test
    fun remaining_values_are_not_emitted_WHEN_cleared() {
        var count = 0

        lateinit var serializer: Serializer<Int>
        serializer =
            serializer { value ->
                count++
                if (value == 1) {
                    serializer.accept(2)
                    serializer.clear()
                }

                true
            }

        serializer.accept(1)

        assertEquals(1, count)
    }

    private fun assertValues(vararg values: Int?) {
        assertEquals(values.toList(), this.values)
    }
}
