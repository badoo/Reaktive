package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.test.doInBackgroundBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class CurrentThreadTest {

    @Test
    fun currentThreadId_returns_same_value_for_main_thread() {
        val id1 = currentThreadId
        val id2 = currentThreadId
        val id3 = currentThreadId

        assertEquals(id1, id2)
        assertEquals(id2, id3)
    }

    @Test
    fun currentThreadId_returns_same_value_for_worker_thread() {
        val ids = AtomicReference<Array<Long>?>(null)

        doInBackgroundBlocking {
            ids.value = Array(3) { currentThreadId }
        }

        assertNotNull(ids.value)
        val (id1, id2, id3) = ids.value!!
        assertEquals(id1, id2)
        assertEquals(id2, id3)
    }

    @Test
    fun currentThreadId_returns_different_values_for_main_and_worker_thread() {
        val mainThreadId = currentThreadId
        val workerThreadId = AtomicReference<Long?>(null)

        doInBackgroundBlocking {
            workerThreadId.value = currentThreadId
        }

        assertNotNull(workerThreadId.value)
        assertNotEquals(mainThreadId, workerThreadId.value)
    }

    @Test
    fun currentThreadId_returns_different_values_for_different_worker_threads() {
        val workerThreadId1 = AtomicReference<Long?>(null)
        val workerThreadId2 = AtomicReference<Long?>(null)

        doInBackgroundBlocking {
            workerThreadId1.value = currentThreadId
        }

        doInBackgroundBlocking {
            workerThreadId2.value = currentThreadId
        }

        assertNotNull(workerThreadId1.value)
        assertNotNull(workerThreadId2.value)
        assertNotEquals(workerThreadId1.value, workerThreadId2.value)
    }
}
