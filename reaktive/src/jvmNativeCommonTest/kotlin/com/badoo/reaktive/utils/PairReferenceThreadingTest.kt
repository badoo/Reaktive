package com.badoo.reaktive.utils

import com.badoo.reaktive.test.doInBackgroundBlocking
import kotlin.test.Test
import kotlin.test.assertSame

class PairReferenceThreadingTest {

    @Test
    fun returns_correct_initial_values_from_background_thread() {
        val obj1 = Any()
        val obj2 = Any()
        val ref = PairReference(obj1, obj2)

        doInBackgroundBlocking {
            assertSame(obj1, ref.first)
            assertSame(obj2, ref.second)
        }
    }

    @Test
    fun updates_and_reads_value_from_different_threads() {
        val obj1 = Any()
        val obj2 = Any()
        val ref = PairReference(Any(), Any())

        doInBackgroundBlocking {
            ref.first = obj1
            ref.second = obj2
        }

        assertSame(obj1, ref.first)
        assertSame(obj2, ref.second)
    }
}
