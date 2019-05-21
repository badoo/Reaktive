package com.badoo.reaktive.observable

import com.badoo.reaktive.testutils.TestObservable
import com.badoo.reaktive.testutils.TestObservableObserver
import com.badoo.reaktive.testutils.dispose
import com.badoo.reaktive.testutils.hasOnNext
import com.badoo.reaktive.testutils.hasSubscribers
import com.badoo.reaktive.testutils.isCompleted
import com.badoo.reaktive.testutils.isError
import com.badoo.reaktive.testutils.test
import com.badoo.reaktive.testutils.values
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConcatMapTest :  UpstreamDownstreamGenericTests by UpstreamDownstreamGenericTests<Unit>({ concatMap { TestObservable<Int>() } }){

    private val upstream = TestObservable<Int?>()

    @Test
    fun subscribes_to_upstream() {
        concatMapUpstreamAndSubscribe { TestObservable() }

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun subscribes_to_first_inner_source() {
        val inner = TestObservable<String>()
        concatMapUpstreamAndSubscribe { inner }

        upstream.onNext(0)

        assertTrue(inner.hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_second_inner_source_WHEN_first_inner_source_is_not_finished() {
        val inners = createInnerSources(2)
        concatMapUpstreamAndSubscribe(inners)

        upstream.onNext(0)
        upstream.onNext(1)

        assertFalse(inners[1].hasSubscribers)
    }

    @Test
    fun subscribes_to_second_source_WHEN_upstream_emitted_second_value_and_first_source_is_finished() {
        val inners = createInnerSources(2)
        concatMapUpstreamAndSubscribe(inners)

        upstream.onNext(0)
        upstream.onNext(1)
        inners[0].onComplete()

        assertTrue(inners[1].hasSubscribers)
    }

    @Test
    fun subscribes_to_second_source_WHEN_first_source_is_finished_and_upstream_emitted_second_value() {
        val inners = createInnerSources(2)
        concatMapUpstreamAndSubscribe(inners)

        upstream.onNext(0)
        inners[0].onComplete()
        upstream.onNext(1)

        assertTrue(inners[1].hasSubscribers)
    }

    @Test
    fun produces_values_in_correct_order() {
        val inners = createInnerSources(3)
        val observer = concatMapUpstreamAndSubscribe() { inners[it ?: 0] }

        upstream.onNext(null)
        inners[0].onNext("0a")
        upstream.onNext(1)
        inners[0].onNext("0b")
        inners[0].onNext("0c")
        inners[0].onComplete()
        inners[1].onNext("1a")
        inners[1].onNext(null)
        inners[1].onNext("1b")
        inners[1].onComplete()
        upstream.onNext(2)
        inners[2].onNext(null)
        inners[2].onNext("2a")
        upstream.onComplete()
        inners[2].onNext(null)
        inners[2].onComplete()

        assertEquals(listOf("0a", "0b", "0c", "1a", null, "1b", null, "2a", null), observer.values)
    }

    @Test
    fun completes_WHEN_upstream_completed_without_values() {
        val observer = concatMapUpstreamAndSubscribe { TestObservable() }

        upstream.onComplete()

        assertTrue(observer.isCompleted)
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed() {
        val observer = concatMapUpstreamAndSubscribe { TestObservable() }

        upstream.onNext(0)
        upstream.onComplete()

        assertFalse(observer.isCompleted)
    }

    @Test
    fun completes_WHEN_upstream_produced_values_and_completed_and_all_sources_are_completed() {
        val inners = createInnerSources(3)
        val observer = concatMapUpstreamAndSubscribe(inners)

        upstream.onNext(0)
        inners[0].onComplete()
        upstream.onNext(1)
        inners[1].onComplete()
        upstream.onNext(2)
        upstream.onComplete()
        inners[2].onComplete()

        assertTrue(observer.isCompleted)
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed_and_not_all_inner_sources_are_completed() {
        val inners = createInnerSources(3)
        val observer = concatMapUpstreamAndSubscribe(inners)

        upstream.onNext(0)
        inners[0].onComplete()
        upstream.onNext(1)
        upstream.onNext(2)
        upstream.onComplete()
        inners[1].onComplete()

        assertFalse(observer.isCompleted)
    }

    @Test
    fun produces_error_WHEN_inner_source_produced_error() {
        val inner = TestObservable<String>()
        val observer = concatMapUpstreamAndSubscribe { inner }

        upstream.onNext(0)
        inner.onError(Throwable())

        assertTrue(observer.isError)
    }

    @Test
    fun does_not_produce_more_values_WHEN_disposed() {
        val inner = TestObservable<String>()
        val observer = concatMapUpstreamAndSubscribe { inner }
        upstream.onNext(0)
        inner.onNext("a")
        observer.reset()

        observer.dispose()
        inner.onNext("b")

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun disposes_source_WHEN_disposed() {
        val inner = TestObservable<String>()
        val observer = concatMapUpstreamAndSubscribe { inner }
        upstream.onNext(0)

        observer.dispose()

        assertTrue(inner.isDisposed)
    }

    @Test
    fun disposes_source_WHEN_upstream_produced_error() {
        val inner = TestObservable<String>()
        concatMapUpstreamAndSubscribe { inner }
        upstream.onNext(0)

        upstream.onError(Throwable())

        assertTrue(inner.isDisposed)
    }

    @Test
    fun disposes_upstream_WHEN_source_produced_error() {
        val inner = TestObservable<String>()
        concatMapUpstreamAndSubscribe { inner }
        upstream.onNext(0)

        inner.onError(Throwable())

        assertTrue(upstream.isDisposed)
    }

    private fun concatMapUpstreamAndSubscribe(innerSources: List<Observable<String?>>): TestObservableObserver<String?> =
        concatMapUpstreamAndSubscribe { innerSources[it!!] }

    private fun concatMapUpstreamAndSubscribe(mapper: (Int?) -> Observable<String?>): TestObservableObserver<String?> =
        upstream.concatMap(mapper).test()

    private fun createInnerSources(count: Int): List<TestObservable<String?>> =
        List(count) { TestObservable<String?>() }
}