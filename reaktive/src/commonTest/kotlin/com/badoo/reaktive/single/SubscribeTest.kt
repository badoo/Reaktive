package com.badoo.reaktive.single

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SubscribeTest {

    private val upstream = TestSingle<Int?>()
    private val observer = TestSingleObserver<Int?>()

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun returned_disposable_is_not_disposed() {
        assertFalse(upstream.subscribe().isDisposed)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_disposed() {
        upstream.subscribe().dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun calls_onSubscribe() {
        upstream.subscribe(onSubscribe = observer::onSubscribe)

        observer.assertSubscribed()
    }

    @Test
    fun calls_onSuccess_WHEN_upstream_succeeded_with_non_null_value() {
        observer.onSubscribe(Disposable())
        upstream.subscribe(onSuccess = observer::onSuccess)

        upstream.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun calls_onSuccess_WHEN_upstream_succeeded_with_null_value() {
        observer.onSubscribe(Disposable())
        upstream.subscribe(onSuccess = observer::onSuccess)

        upstream.onSuccess(null)

        observer.assertSuccess(null)
    }

    @Test
    fun disposes_disposable_WHEN_upstream_is_succeeded() {
        val disposable = upstream.subscribe()

        upstream.onSuccess(0)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun calls_onError_WHEN_upstream_produced_an_error() {
        observer.onSubscribe(Disposable())
        upstream.subscribe(onError = observer::onError)
        val error = Throwable()

        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun disposes_disposable_WHEN_upstream_produced_an_error() {
        val disposable = upstream.subscribe(onError = {})

        upstream.onError(Throwable())

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun calls_onError_WHEN_onSubscribe_thrown_exception() {
        val exception = Exception()
        val caughtException: AtomicReference<Throwable?> = AtomicReference(null)

        upstream.subscribe(
            onSubscribe = { throw exception },
            onError = { caughtException.value = it }
        )

        assertSame(exception, caughtException.value)
    }

    @Test
    fun returned_disposable_is_disposed_WHEN_onSubscribe_thrown_exception() {
        val exception = Exception()

        val disposable =
            upstream.subscribe(
                onSubscribe = { throw exception },
                onError = {}
            )

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun does_not_subscribe_to_upstream_WHEN_onSubscribe_thrown_exception() {
        upstream.subscribe(
            onSubscribe = { throw Exception() },
            onError = {}
        )

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun calls_uncaught_exception_handler_WHEN_onSuccess_thrown_exception() {
        val exception = Exception()
        val caughtException: AtomicReference<Throwable?> = AtomicReference(null)
        reaktiveUncaughtErrorHandler = { caughtException.value = it }

        upstream.subscribe(onSuccess = { throw exception })
        upstream.onSuccess(0)

        assertSame(exception, caughtException.value)
    }

    @Test
    fun does_not_call_onError_WHEN_onSuccess_thrown_exception() {
        val exception = Exception()
        reaktiveUncaughtErrorHandler = {}
        val isOnErrorCalled = AtomicBoolean()

        upstream.subscribe(
            onError = { isOnErrorCalled.value = true },
            onSuccess = { throw exception }
        )
        upstream.onSuccess(0)

        assertFalse(isOnErrorCalled.value)
    }

    @Test
    fun calls_uncaught_exception_handler_with_CompositeException_WHEN_onError_thrown_exception() {
        val exception1 = Exception()
        val exception2 = Exception()
        val caughtException: AtomicReference<Throwable?> = AtomicReference(null)
        reaktiveUncaughtErrorHandler = { caughtException.value = it }

        upstream.subscribe(onError = { throw exception2 })
        upstream.onError(exception1)

        assertTrue(caughtException.value is CompositeException)
        val compositeException = caughtException.value as CompositeException
        assertSame(exception1, compositeException.cause1)
        assertSame(exception2, compositeException.cause2)
    }
}
