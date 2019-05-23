package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.dispose
import com.badoo.reaktive.test.observable.hasOnNext
import com.badoo.reaktive.test.observable.hasSubscribers
import com.badoo.reaktive.test.observable.isCompleted
import com.badoo.reaktive.test.observable.isError
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.observable.values
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlatMapTest : UpstreamDownstreamGenericTests by UpstreamDownstreamGenericTests<Unit>({ flatMap { TestObservable<Int>() } }) {

    private val source = TestObservable<Int?>()

    @Test
    fun subscribes_to_upstream() {
        flatMapUpstreamAndSubscribe { TestObservable() }

        assertTrue(source.hasSubscribers)
    }

    @Test
    fun subscribes_to_inner_sources() {
        val inners = createInnerSources(2)
        flatMapUpstreamAndSubscribe(inners)

        source.onNext(0)
        source.onNext(1)

        assertTrue(inners[0].hasSubscribers)
        assertTrue(inners[1].hasSubscribers)
    }

    @Test
    fun produces_values_in_correct_order() {
        val inners = createInnerSources(3)
        val observer = flatMapUpstreamAndSubscribe { inners[it ?: 0] }

        source.onNext(null)
        inners[0].onNext("0a")
        source.onNext(1)
        inners[0].onNext("0b")
        inners[1].onNext("1a")
        inners[0].onNext("0c")
        source.onNext(2)
        inners[1].onNext(null)
        inners[0].onComplete()
        inners[2].onNext(null)
        inners[1].onNext("1b")
        inners[1].onComplete()
        inners[2].onNext("2b")
        source.onComplete()
        inners[2].onNext("2c")
        inners[2].onComplete()

        assertEquals(listOf("0a", "0b", "1a", "0c", null, null, "1b", "2b", "2c"), observer.values)
    }

    @Test
    fun completes_WHEN_upstream_completed_without_values() {
        val observer = flatMapUpstreamAndSubscribe { TestObservable() }

        source.onComplete()

        assertTrue(observer.isCompleted)
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed() {
        val observer = flatMapUpstreamAndSubscribe { TestObservable() }

        source.onNext(0)
        source.onComplete()

        assertFalse(observer.isCompleted)
    }

    @Test
    fun completes_WHEN_upstream_produced_values_and_completed_and_all_inner_sources_are_completed() {
        val inners = createInnerSources(3)
        val observer = flatMapUpstreamAndSubscribe(inners)

        source.onNext(0)
        inners[0].onComplete()
        source.onNext(1)
        inners[1].onComplete()
        source.onNext(2)
        source.onComplete()
        inners[2].onComplete()

        assertTrue(observer.isCompleted)
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed_and_not_all_inner_sources_are_completed() {
        val inners = createInnerSources(3)
        val observer = flatMapUpstreamAndSubscribe(inners)

        source.onNext(0)
        inners[0].onComplete()
        source.onNext(1)
        source.onNext(2)
        source.onComplete()
        inners[2].onComplete()

        assertFalse(observer.isCompleted)
    }

    @Test
    fun produces_error_WHEN_inner_source_produced_error() {
        val inners = createInnerSources(2)
        val observer = flatMapUpstreamAndSubscribe(inners)
        val error = Throwable()

        source.onNext(1)
        inners[1].onError(error)

        assertTrue(observer.isError(error))
    }

    @Test
    fun does_not_produce_more_values_WHEN_disposed() {
        val inner = TestObservable<String>()
        val observer = flatMapUpstreamAndSubscribe { inner }
        source.onNext(0)
        inner.onNext("a")
        observer.reset()

        observer.dispose()
        inner.onNext("b")

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun disposes_streams_WHEN_disposed() {
        val inners = createInnerSources(2)
        val observer = flatMapUpstreamAndSubscribe(inners)
        source.onNext(0)
        source.onNext(1)

        observer.dispose()

        assertTrue(source.isDisposed)
        assertTrue(inners[0].isDisposed)
        assertTrue(inners[1].isDisposed)
    }

    @Test
    fun disposes_streams_WHEN_upstream_produced_error() {
        val inners = createInnerSources(2)
        flatMapUpstreamAndSubscribe(inners)
        source.onNext(0)
        source.onNext(1)

        source.onError(Throwable())

        assertTrue(source.isDisposed)
        assertTrue(inners[0].isDisposed)
        assertTrue(inners[1].isDisposed)
    }

    @Test
    fun disposes_streams_WHEN_inner_source_produced_error() {
        val inners = createInnerSources(2)
        flatMapUpstreamAndSubscribe(inners)
        source.onNext(0)
        source.onNext(1)

        inners[1].onError(Throwable())

        assertTrue(source.isDisposed)
        assertTrue(inners[0].isDisposed)
        assertTrue(inners[1].isDisposed)
    }

    private fun flatMapUpstreamAndSubscribe(innerSources: List<Observable<String?>>): TestObservableObserver<String?> =
        flatMapUpstreamAndSubscribe { innerSources[it!!] }

    private fun flatMapUpstreamAndSubscribe(mapper: (Int?) -> Observable<String?>): TestObservableObserver<String?> =
        source.flatMap(mapper).test()

    private fun createInnerSources(count: Int): List<TestObservable<String?>> =
        List(count) { TestObservable<String?>() }
}