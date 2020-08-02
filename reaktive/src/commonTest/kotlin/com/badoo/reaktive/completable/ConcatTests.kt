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

class ConcatTests : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ concat(this) }) {

    private val upstream1 = TestCompletable()
    private val upstream2 = TestCompletable()
    private val inner = concat(upstream1, upstream2).test()

    @Test
    fun subscribes_to_first_upstream_WHEN_subscribed() {
        assertTrue(upstream1.hasSubscribers)
    }

    @Test
    fun does_not_subscribes_to_second_upstream_WHEN_subscribed() {
        assertFalse(upstream2.hasSubscribers)
    }

    @Test
    fun subscribes_to_second_upstream_WHEN_first_upstream_is_completed() {
        upstream1.onComplete()

        assertTrue(upstream2.hasSubscribers)
    }

    @Test
    fun does_not_subscribes_to_second_upstream_WHEN_first_upstream_has_error() {
        upstream1.onError(Throwable())

        assertFalse(upstream2.hasSubscribers)
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
    fun completes_WHEN_second_upstream_is_completed() {
        upstream1.onComplete()
        upstream2.onComplete()

        inner.assertComplete()
    }

    @Test
    fun unsubscribes_from_second_upstream_WHEN_disposed() {
        upstream1.onComplete()
        inner.dispose()

        assertFalse(upstream2.hasSubscribers)
    }

    @Test
    fun completed_WHEN_sources_are_empty() {
        val observer = emptyList<Completable>().concat().test()

        observer.assertComplete()
    }
}
