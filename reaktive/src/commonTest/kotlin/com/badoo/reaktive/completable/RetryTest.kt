package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.disposeIfTerminalEvent
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RetryTest : CompletableToCompletableTests by CompletableToCompletableTests({ retry() }) {

    private val upstream = TestCompletable()

    @Test
    @Ignore
    override fun produces_error_WHEN_upstream_produced_error() {
        // not applicable
    }

    @Test
    fun resubscribes_WHEN_upstream_produces_error_and_predicate_returns_true() {
        val observer = upstream.retry().test()
        upstream.onError(Throwable())
        observer.disposeIfTerminalEvent()
        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun does_not_resubscribe_WHEN_upstream_produces_error_and_predicate_returns_false() {
        val observer = upstream.retry { _, _ -> false }.test()
        upstream.onError(Throwable())
        observer.disposeIfTerminalEvent()
        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun produces_error_WHEN_upstream_produces_error_and_predicate_returns_false() {
        val observer = upstream.retry { _, _ -> false }.test()
        val throwable = Throwable()
        upstream.onError(throwable)
        assertSame(observer.error, throwable)
    }

    @Test
    fun completes_WHEN_upstream_completes_after_retry() {
        val observer = upstream.retry().test()
        upstream.onError(Throwable())
        upstream.onComplete()
        assertTrue(observer.isComplete)
    }

    @Test
    fun predicate_receives_valid_throwable_WHEN_upstream_produces_error() {
        val throwable1 = Throwable()
        val throwable2 = Throwable()
        val throwableRef = AtomicReference<Throwable?>(null)
        upstream
            .retry { _, throwable ->
                throwableRef.value = throwable
                true
            }
            .test()
        upstream.onError(throwable1)
        assertSame(throwable1, throwableRef.value)
        upstream.onError(throwable2)
        assertSame(throwable2, throwableRef.value)
    }

    @Test
    fun predicate_receives_valid_counter_WHEN_upstream_produces_error() {
        val timeRef = AtomicInt()
        upstream
            .retry { time, _ ->
                timeRef.value = time
                true
            }
            .test()
        upstream.onError(Throwable())
        assertSame(timeRef.value, 0)
        upstream.onError(Throwable())
        assertSame(timeRef.value, 1)
    }
}