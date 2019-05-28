package com.badoo.reaktive.single

import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.isError
import com.badoo.reaktive.test.single.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SubscribeTest {

    private val upstream = TestSingle<Int?>()

    @Test
    fun returned_disposable_is_not_disposed() {
        assertFalse(upstream.subscribe().isDisposed)
    }

    @Test
    fun disposes_upstream_WHEN_disposed() {
        upstream.subscribe().dispose()

        assertTrue(upstream.isDisposed)
    }

    @Test
    fun calls_onSubscribe() {
        val observer = TestSingleObserver<Unit>()

        upstream.subscribe(onSubscribe = observer::onSubscribe)

        assertEquals(1, observer.disposables.size)
    }

    @Test
    fun calls_onSuccess_WHEN_upstream_succeeded_with_non_null_value() {
        val observer = TestSingleObserver<Int?>()
        upstream.subscribe(onSuccess = observer::onSuccess)

        upstream.onSuccess(0)

        assertEquals(0, observer.value)
    }

    @Test
    fun calls_onSuccess_WHEN_upstream_succeeded_with_null_value() {
        val observer = TestSingleObserver<Int?>()
        upstream.subscribe(onSuccess = observer::onSuccess)

        upstream.onSuccess(null)

        assertNull(observer.value)
    }

    @Test
    fun disposes_disposable_WHEN_upstream_is_succeeded() {
        val disposable = upstream.subscribe()

        upstream.onSuccess(0)

        assertTrue(disposable.isDisposed)
    }


    @Test
    fun calls_onError_WHEN_upstream_produced_an_error() {
        val observer = TestSingleObserver<Unit>()
        upstream.subscribe(onError = observer::onError)
        val error = Throwable()

        upstream.onError(error)

        assertTrue(observer.isError(error))
    }

    @Test
    fun disposes_disposable_WHEN_upstream_produced_an_error() {
        val disposable = upstream.subscribe(onError = {})

        upstream.onError(Throwable())

        assertTrue(disposable.isDisposed)
    }
}