package com.badoo.reaktive.completable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.TestCompletableObserver
import com.badoo.reaktive.test.completable.assertComplete
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

    private val upstream = TestCompletable()
    private val observer = TestCompletableObserver()

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
    fun calls_onComplete_WHEN_upstream_is_completed() {
        observer.onSubscribe(disposable())
        upstream.subscribe(onComplete = observer::onComplete)

        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun calls_onError_WHEN_upstream_produced_an_error() {
        observer.onSubscribe(disposable())
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
    fun does_not_call_onError_WHEN_onComplete_thrown_exception() {
        val exception = Exception()
        reaktiveUncaughtErrorHandler = {}
        val isOnErrorCalled = AtomicBoolean()

        upstream.subscribe(
            onError = { isOnErrorCalled.value = true },
            onComplete = { throw exception }
        )
        upstream.onComplete()

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
