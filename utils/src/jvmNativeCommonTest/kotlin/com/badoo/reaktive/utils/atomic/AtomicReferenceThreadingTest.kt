package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.test.doInBackgroundBlocking
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AtomicReferenceThreadingTest {

    @Test
    fun returns_correct_initial_value_from_background_thread() {
        val obj = Any()
        val ref = AtomicReference(obj)

        doInBackgroundBlocking {
            assertSame(obj, ref.value)
        }
    }

    @Test
    fun updates_and_reads_value_from_different_threads() {
        val obj = Any()
        val ref = AtomicReference(Any())

        doInBackgroundBlocking {
            ref.value = obj
        }

        assertSame(obj, ref.value)
    }

    @Test
    fun compareAndSet_with_different_value_from_background_thread() {
        val obj = Any()
        val ref = AtomicReference(obj)

        doInBackgroundBlocking {
            assertFalse(ref.compareAndSet(Any(), Any()))
            assertSame(obj, ref.value)
        }
    }

    @Test
    fun compareAndSet_with_same_value_from_background_thread() {
        val obj = Any()
        val ref = AtomicReference(obj)

        doInBackgroundBlocking {
            val newObj = Any()
            assertTrue(ref.compareAndSet(obj, newObj))
            assertSame(newObj, ref.value)
        }
    }
}
