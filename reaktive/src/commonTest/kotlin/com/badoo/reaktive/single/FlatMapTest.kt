package com.badoo.reaktive.single

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.assertNotSuccess
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlatMapTest : SingleToSingleTests by SingleToSingleTestsImpl({ flatMap { TestSingle<Int>() } }) {

    private val upstream = TestSingle<Int?>()
    private val inner = TestSingle<String?>()
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
    fun does_not_succeeds_WHEN_inner_succeeded_after_dispose() {
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

    private fun flatMapUpstreamAndSubscribe(innerSources: List<Single<String?>>): TestSingleObserver<String?> =
        flatMapUpstreamAndSubscribe { innerSources[it!!] }

    private fun flatMapUpstreamAndSubscribe(mapper: (Int?) -> Single<String?>): TestSingleObserver<String?> =
        upstream.flatMap(mapper).test()
}
