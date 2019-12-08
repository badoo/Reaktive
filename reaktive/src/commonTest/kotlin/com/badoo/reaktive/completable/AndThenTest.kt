package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.assertNotComplete
import com.badoo.reaktive.test.completable.test
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AndThenTest : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ andThen(TestCompletable()) }) {

    private val upstream = TestCompletable()
    private val inner = TestCompletable()
    private val observer = upstream.andThen(inner).test()

    @Ignore
    @Test
    override fun completes_WHEN_upstream_completed() {
        // not applicable
    }

    @Ignore
    @Test
    override fun disposes_downstream_disposable_WHEN_upstream_completed() {
        // not applicable
    }

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

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_inner_source_produced_error() {
        val error = Throwable()

        upstream.onComplete()
        inner.onError(error)

        observer.assertError(error)
    }

    @Test
    fun does_not_complete_WHEN_inner_succeeded_after_dispose() {
        upstream.onComplete()
        observer.reset()
        observer.dispose()

        inner.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun unsubscribes_from_inner_source_WHEN_disposed() {
        upstream.onComplete()

        observer.dispose()

        assertFalse(inner.hasSubscribers)
    }
}
