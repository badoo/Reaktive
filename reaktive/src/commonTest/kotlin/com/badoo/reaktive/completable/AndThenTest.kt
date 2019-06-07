package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.isCompleted
import com.badoo.reaktive.test.completable.isError
import com.badoo.reaktive.test.completable.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AndThenTest : UpstreamDownstreamGenericTests by UpstreamDownstreamGenericTests({ andThen(TestCompletable()) }) {

    private val upstream = TestCompletable()
    private val inner = TestCompletable()
    private val observer = upstream.andThen(inner).test()

    @Test
    fun subscribes_to_upstream() {
        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_inner_source_WHEN_upstream_has_not_completed() {
        assertFalse(inner.hasSubscribers)
    }

    @Test
    fun subscribes_to_inner_source_WHEN_upstream_completed() {
        upstream.onComplete()

        assertTrue(inner.hasSubscribers)
    }

    @Test
    fun succeeds_WHEN_inner_source_completed() {
        upstream.onComplete()
        inner.onComplete()

        assertTrue(observer.isCompleted)
    }

    @Test
    fun produces_error_WHEN_inner_source_produced_error() {
        val error = Throwable()

        upstream.onComplete()
        inner.onError(error)

        assertTrue(observer.isError(error))
    }

    @Test
    fun does_not_complete_WHEN_inner_succeeded_after_dispose() {
        upstream.onComplete()
        observer.reset()
        observer.dispose()

        inner.onComplete()

        assertFalse(observer.isCompleted)
    }

    @Test
    fun disposes_inner_source_WHEN_disposed() {
        upstream.onComplete()

        observer.dispose()

        assertTrue(upstream.isDisposed)
        assertTrue(inner.isDisposed)
    }

}