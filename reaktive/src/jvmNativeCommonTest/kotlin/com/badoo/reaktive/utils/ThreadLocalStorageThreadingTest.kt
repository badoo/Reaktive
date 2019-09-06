package com.badoo.reaktive.utils

import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals

class ThreadLocalStorageThreadingTest {

    @Test
    fun throws_RuntimeException_WHEN_get_value_from_another_thread() {
        testThrowsRuntimeExceptionFromAnotherThread { it.get() }
    }

    @Test
    fun throws_RuntimeException_WHEN_set_value_from_another_thread() {
        testThrowsRuntimeExceptionFromAnotherThread { it.set(Unit) }
    }

    @Test
    fun throws_RuntimeException_WHEN_dispose_from_another_thread() {
        testThrowsRuntimeExceptionFromAnotherThread { it.dispose() }
    }

    @Test
    fun throws_RuntimeException_WHEN_check_isDisposed_from_another_thread() {
        testThrowsRuntimeExceptionFromAnotherThread { it.isDisposed }
    }

    private fun testThrowsRuntimeExceptionFromAnotherThread(block: (ThreadLocalStorage<Unit>) -> Unit) {
        val storage = ThreadLocalStorage(Unit)
        val isFailed = AtomicReference<Boolean?>(null)

        doInBackgroundBlocking {
            isFailed.value =
                try {
                    block(storage)
                    false
                } catch (e: RuntimeException) {
                    true
                }
        }

        assertEquals(true, isFailed.value)
    }
}