package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.TestMaybeObserver
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertNotComplete
import com.badoo.reaktive.test.maybe.assertNotSuccess
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlatMapTest : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ flatMap { TestMaybe<Int>() } }) {

    private val upstream = TestMaybe<Int?>()
    private val inner = TestMaybe<String?>()
    private val observer = flatMapUpstreamAndSubscribe(listOf(inner))

    @Ignore
    @Test
    override fun disposes_downstream_disposable_WHEN_upstream_succeeded() {
        // not applicable
    }

    @Test
    fun subscribes_to_upstream() {
        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun subscribes_to_inner_source_WHEN_upstream_succeeded() {
        upstream.onSuccess(0)

        assertTrue(inner.hasSubscribers)
    }

    @Test
    fun completes_WHEN_inner_source_completed() {
        val observer = flatMapUpstreamAndSubscribe { inner }

        upstream.onComplete()
        inner.onComplete()

        observer.assertComplete()
    }

    @Test
    fun succeeds_WHEN_inner_source_succeeded_with_non_null_value() {
        upstream.onSuccess(0)
        inner.onSuccess("1")

        observer.assertSuccess("1")
    }

    @Test
    fun succeeds_WHEN_inner_source_succeeded_with_null_value() {
        val observer = flatMapUpstreamAndSubscribe { inner }

        upstream.onSuccess(null)
        inner.onSuccess("1")

        observer.assertSuccess("1")
    }

    @Test
    fun produces_error_WHEN_inner_source_produced_error() {
        val error = Throwable()

        upstream.onSuccess(0)
        inner.onError(error)

        observer.assertError(error)
    }

    @Test
    fun does_not_complete_WHEN_inner_completed_after_dispose() {
        upstream.onComplete()
        observer.reset()
        observer.dispose()

        inner.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun does_not_succeed_WHEN_inner_succeeded_after_dispose() {
        upstream.onSuccess(0)
        observer.reset()
        observer.dispose()

        inner.onSuccess("1")

        observer.assertNotSuccess()
    }

    @Test
    fun unsubscribes_from_inner_source_WHEN_disposed() {
        upstream.onSuccess(0)

        observer.dispose()

        assertFalse(inner.hasSubscribers)
    }

    private fun flatMapUpstreamAndSubscribe(innerSources: List<Maybe<String?>>): TestMaybeObserver<String?> =
        flatMapUpstreamAndSubscribe { innerSources[it!!] }

    private fun flatMapUpstreamAndSubscribe(mapper: (Int?) -> Maybe<String?>): TestMaybeObserver<String?> =
        upstream.flatMap(mapper).test()
}
