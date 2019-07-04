package com.badoo.reaktive.single

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test
import kotlin.test.assertTrue

class FlattenTest {

    private val upstream = TestSingle<Iterable<Int>>()
    private val observer = upstream.flatten().test()

    @Test
    fun emits_values_WHEN_upstream_succeeded() {
        upstream.onSuccess(listOf(1, 2, 3, 4, 5))

        observer.assertValues(1, 2, 3, 4, 5)
    }

    @Test
    fun does_not_complete_WHEN_upstream_not_succeeded() {
        observer.assertNotComplete()
    }

    @Test
    fun completes_WHEN_upstream_succeeded_with_values() {
        upstream.onSuccess(listOf(1))

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_upstream_succeeded_without_values() {
        upstream.onSuccess(emptyList())

        observer.assertComplete()
    }

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed() {
        observer.assertSubscribed()
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error() {
        val error = Throwable()
        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun disposes_upstream_WHEN_disposed() {
        observer.dispose()

        assertTrue(upstream.isDisposed)
    }
}