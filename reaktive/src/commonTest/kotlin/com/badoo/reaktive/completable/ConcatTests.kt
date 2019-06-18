package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.isComplete
import com.badoo.reaktive.test.completable.isError
import com.badoo.reaktive.test.completable.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConcatTests : CompletableToCompletableTests by CompletableToCompletableTests({ concat(this) }) {

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
    fun subscribes_to_second_upstream_WHEN_first_is_completed() {
        upstream1.onComplete()

        assertTrue(upstream2.hasSubscribers)
    }

    @Test
    fun does_not_subscribes_to_second_upstream_WHEN_first_has_error() {
        upstream1.onError(Throwable())

        assertFalse(upstream2.hasSubscribers)
    }

    @Test
    fun produces_error_WHEN_second_upstream_produced_error() {
        val throwable = Throwable()
        upstream1.onComplete()
        upstream2.onError(throwable)

        assertTrue(inner.isError(throwable))
    }

    @Test
    fun does_not_complete_WHEN_first_is_completed() {
        upstream1.onComplete()

        assertFalse(inner.isComplete)
    }

    @Test
    fun completes_WHEN_second_is_completed() {
        upstream1.onComplete()
        upstream2.onComplete()

        assertTrue(inner.isComplete)
    }

    @Test
    fun disposes_second_upstream_WHEN_disposed() {
        upstream1.onComplete()
        inner.dispose()

        assertTrue(upstream2.isDisposed)
    }

}