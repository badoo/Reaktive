package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.assertNotComplete
import com.badoo.reaktive.test.completable.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MergeTests : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ merge(this) }) {

    private val upstream1 = TestCompletable()
    private val upstream2 = TestCompletable()
    private val inner = merge(upstream1, upstream2).test()

    @Test
    fun subscribes_to_first_upstream_WHEN_subscribed() {
        assertTrue(upstream1.hasSubscribers)
    }

    @Test
    fun subscribes_to_second_upstream_WHEN_subscribed() {
        assertTrue(upstream2.hasSubscribers)
    }

    @Test
    fun produces_error_WHEN_first_upstream_produced_error() {
        val throwable = Throwable()
        upstream1.onError(throwable)

        inner.assertError(throwable)
    }

    @Test
    fun produces_error_WHEN_second_upstream_produced_error() {
        val throwable = Throwable()
        upstream1.onComplete()
        upstream2.onError(throwable)

        inner.assertError(throwable)
    }

    @Test
    fun does_not_complete_WHEN_first_upstream_is_completed() {
        upstream1.onComplete()

        inner.assertNotComplete()
    }

    @Test
    fun does_not_complete_WHEN_second_upstream_is_completed() {
        upstream2.onComplete()

        inner.assertNotComplete()
    }

    @Test
    fun completes_WHEN_both_upstreams_are_completed() {
        upstream1.onComplete()
        upstream2.onComplete()

        inner.assertComplete()
    }

    @Test
    fun unsubscribes_from_both_upstreams_WHEN_disposed() {
        inner.dispose()

        assertFalse(upstream1.hasSubscribers)
        assertFalse(upstream2.hasSubscribers)
    }

}
