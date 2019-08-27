package com.badoo.reaktive.utils

import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.test.waitForOrFail
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertTrue

class ThreadLocalStorageThreadingTest {

    @Test
    fun throws_RuntimeException_WHEN_get_value_from_another_thread() {
        testThrowsRuntimeExceptionFromAnotherThread { it.value }
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
        val isFailedRef = AtomicReference<Boolean?>(null)
        val lock = Lock()
        val condition = lock.newCondition()

        computationScheduler.newExecutor().submit {
            val isFailed =
                try {
                    block(storage)
                    false
                } catch (e: RuntimeException) {
                    true
                }

            lock.synchronized {
                isFailedRef.value = isFailed
                condition.signal()
            }
        }

        lock.synchronized {
            condition.waitForOrFail(5_000_000_000L) {
                isFailedRef.value != null
            }
        }

        assertTrue(isFailedRef.value!!)
    }
}