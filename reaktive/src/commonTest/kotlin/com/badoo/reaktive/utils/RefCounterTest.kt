package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RefCounterTest {

    private var isDestroyed = AtomicBoolean()
    private val refCounter = RefCounter { isDestroyed.value = true }

    @Test
    fun does_not_destroy_WHEN_created() {
        assertFalse(isDestroyed.value)
    }

    @Test
    fun does_not_destroy_WHEN_retained_once() {
        refCounter.retain()

        assertFalse(isDestroyed.value)
    }

    @Test
    fun retain_returns_true_WHEN_not_destroyed() {
        val result = refCounter.retain()

        assertTrue(result)
    }

    @Test
    fun destroys_WHEN_released_after_creation() {
        refCounter.release()

        assertTrue(isDestroyed.value)
    }

    @Test
    fun does_not_destroy_WHEN_retained_and_released() {
        refCounter.retain()
        refCounter.release()

        assertFalse(isDestroyed.value)
    }

    @Test
    fun destroys_WHEN_retained_and_released_and_released() {
        refCounter.retain()
        refCounter.release()
        refCounter.release()

        assertTrue(isDestroyed.value)
    }

    @Test
    fun retain_returns_false_WHEN_destroyed() {
        refCounter.release()

        val result = refCounter.retain()

        assertFalse(result)
    }

    @Test
    fun retain_second_time_returns_false_WHEN_destroyed() {
        refCounter.release()
        refCounter.retain()

        val result = refCounter.retain()

        assertFalse(result)
    }

    @Test
    fun does_not_destroy_WHEN_retained_100_times_and_released_100_times() {
        repeat(100) { refCounter.retain() }
        repeat(100) { refCounter.release() }

        assertFalse(isDestroyed.value)
    }

    @Test
    fun destroys_WHEN_retained_100_times_and_released_101_times() {
        repeat(100) { refCounter.retain() }
        repeat(101) { refCounter.release() }

        assertTrue(isDestroyed.value)
    }

    @Test
    fun release_throws_exception_WHEN_destroyed_and_released() {
        refCounter.release()

        assertFailsWith(IllegalStateException::class) {
            refCounter.release()
        }
    }

    @Test
    fun does_not_destroy_WHEN_destroyed_and_retained_and_released() {
        refCounter.release()
        refCounter.retain()
        isDestroyed.value = false

        try {
            refCounter.release()
        } catch (ignored: IllegalStateException) {
        }

        assertFalse(isDestroyed.value)
    }
}
