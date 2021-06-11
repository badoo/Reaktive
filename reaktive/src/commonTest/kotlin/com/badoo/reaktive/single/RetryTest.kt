package com.badoo.reaktive.single

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RetryTest : SingleToSingleTests by SingleToSingleTestsImpl({ retry() }) {

    private val upstream = TestSingle<Int?>()

    @Test
    @Ignore
    override fun produces_error_WHEN_upstream_produced_error() {
        // not applicable
    }

    @Test
    @Ignore
    override fun disposes_downstream_disposable_WHEN_upstream_produced_error() {
        // not applicable
    }

    @Test
    fun resubscribes_WHEN_upstream_produces_error_and_predicate_returns_true() {
        upstream.retry().test()
        upstream.onError(Throwable())
        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun does_not_resubscribe_WHEN_upstream_produces_error_and_predicate_returns_false() {
        upstream.retry { _, _ -> false }.test()
        upstream.onError(Throwable())
        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun produces_error_WHEN_upstream_produces_error_and_predicate_returns_false() {
        val observer = upstream.retry { _, _ -> false }.test()
        val throwable = Throwable()
        upstream.onError(throwable)
        observer.assertError(throwable)
    }

    @Test
    fun produces_value_WHEN_upstream_produces_value_after_retry() {
        val observer = upstream.retry().test()
        upstream.onError(Throwable())
        upstream.onSuccess(1)
        observer.assertSuccess(1)
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
        assertSame(timeRef.value, 1)
        upstream.onError(Throwable())
        assertSame(timeRef.value, 2)
    }

    @Test
    fun produces_error_WHEN_predicate_throw_exception() {
        val throwable1 = Throwable()
        val throwable2 = Throwable()
        val observer = upstream.retry { _, _ -> throw throwable2 }.test()
        upstream.onError(throwable1)
        val compositeThrowable = observer.error
        assertTrue(compositeThrowable is CompositeException)
        assertSame(throwable1, compositeThrowable.cause1)
        assertSame(throwable2, compositeThrowable.cause2)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_predicate_throw_exception() {
        upstream.retry { _, _ -> throw Throwable() }.test()
        upstream.onError(Throwable())
        assertFalse(upstream.hasSubscribers)
    }
}
