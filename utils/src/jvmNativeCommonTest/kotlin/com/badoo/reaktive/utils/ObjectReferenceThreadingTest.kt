package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.test.doInBackgroundBlocking
import kotlin.test.Test
import kotlin.test.assertSame

class ObjectReferenceThreadingTest {

    @Test
    fun returns_correct_initial_value_from_background_thread() {
        val obj = Any()
        val ref = ObjectReference(obj)

        doInBackgroundBlocking {
            assertSame(obj, ref.value)
        }
    }

    @Test
    fun updates_and_reads_value_from_different_threads() {
        val obj = Any()
        val ref = ObjectReference(Any())

        doInBackgroundBlocking {
            ref.value = obj
        }

        assertSame(obj, ref.value)
    }
}
