package com.badoo.reaktive.utils

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
        var ids: Array<Long>? = null

        doInBackgroundBlocking {
            ids = Array(3) { currentThreadId }
        }

        val (id1, id2, id3) = assertNotNull(ids)
        assertEquals(id1, id2)
        assertEquals(id2, id3)
    }

    @Test
    fun currentThreadId_returns_different_values_for_main_and_worker_thread() {
        val mainThreadId = currentThreadId
        var workerThreadId: Long? = null

        doInBackgroundBlocking {
            workerThreadId = currentThreadId
        }

        assertNotNull(workerThreadId)
        assertNotEquals(mainThreadId, workerThreadId)
    }

    @Test
    fun currentThreadId_returns_different_values_for_different_worker_threads() {
        var workerThreadId1: Long? = null
        var workerThreadId2: Long? = null

        doInBackgroundBlocking {
            workerThreadId1 = currentThreadId
        }

        doInBackgroundBlocking {
            workerThreadId2 = currentThreadId
        }

        assertNotNull(workerThreadId1)
        assertNotNull(workerThreadId2)
        assertNotEquals(workerThreadId1, workerThreadId2)
    }
}
