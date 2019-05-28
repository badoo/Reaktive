package com.badoo.reaktive.single

import com.badoo.reaktive.test.observable.isCompleted
import com.badoo.reaktive.test.observable.isError
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.observable.values
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlattenTest {

    private val upstream = TestSingle<Iterable<Int>>()
    private val observer = upstream.flatten().test()

    @Test
    fun emits_values_WHEN_upstream_succeeded() {
        upstream.onSuccess(listOf(1, 2, 3, 4, 5))

        assertEquals(listOf(1, 2, 3, 4, 5), observer.values)
    }

    @Test
    fun does_not_complete_WHEN_upstream_not_succeeded() {
        assertFalse(observer.isCompleted)
    }

    @Test
    fun completes_WHEN_upstream_succeeded_with_values() {
        upstream.onSuccess(listOf(1))

        assertTrue(observer.isCompleted)
    }

    @Test
    fun completes_WHEN_upstream_succeeded_without_values() {
        upstream.onSuccess(emptyList())

        assertTrue(observer.isCompleted)
    }

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed() {
        assertEquals(1, observer.disposables.size)
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error() {
        upstream.onError(Throwable())

        assertTrue(observer.isError)
    }

    @Test
    fun disposes_upstream_WHEN_disposed() {
        observer.dispose()

        assertTrue(upstream.isDisposed)
    }
}