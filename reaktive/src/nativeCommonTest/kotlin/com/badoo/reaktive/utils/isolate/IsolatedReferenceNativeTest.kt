package com.badoo.reaktive.utils.isolate

import com.badoo.reaktive.test.doInBackgroundBlocking
import com.badoo.reaktive.utils.freeze
import com.badoo.reaktive.utils.isFrozen
import kotlin.native.IncorrectDereferenceException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IsolatedReferenceNativeTest {

    @Test
    fun throws_IncorrectDereferenceException_WHEN_value_not_frozen_and_get_value_from_another_thread() {
        val ref = IsolatedReference(Data())

        val error =
            doInBackgroundBlocking {
                try {
                    ref.getOrThrow()
                    null
                } catch (e: Throwable) {
                    e
                }
            }

        assertTrue(error is IncorrectDereferenceException)
    }

    @Test
    fun returns_value_WHEN_value_frozen_and_get_value_from_another_thread() {
        val data = Data().freeze()
        val ref = IsolatedReference(data)

        val result = doInBackgroundBlocking { ref.getOrThrow() }

        assertEquals(data, result)
    }

    @Test
    fun value_not_frozen_WHEN_reference_frozen() {
        val data = Data()
        val ref = IsolatedReference(data)

        ref.freeze()

        assertFalse(data.isFrozen)
    }

    @Test
    fun returns_null_WHEN_disposed() {
        val ref = IsolatedReference(Data())

        ref.dispose()

        assertNull(ref.getOrThrow())
    }

    private class Data
}
