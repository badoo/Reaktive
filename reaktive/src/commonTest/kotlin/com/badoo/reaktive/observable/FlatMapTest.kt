package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlatMapTest
    : ObservableToObservableTests by ObservableToObservableTestsImpl({ flatMap { TestObservable<Int>() } }) {

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

        observer.assertValues("0a", "0b", "1a", "0c", null, null, "1b", "2b", "2c")
    }

    @Test
    fun completes_WHEN_upstream_completed_without_values() {
        val observer = flatMapUpstreamAndSubscribe { TestObservable() }

        source.onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed() {
        val observer = flatMapUpstreamAndSubscribe { TestObservable() }

        source.onNext(0)
        source.onComplete()

        observer.assertNotComplete()
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

        observer.assertComplete()
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

        observer.assertNotComplete()
    }

    @Test
    fun produces_error_WHEN_inner_source_produced_error() {
        val inners = createInnerSources(2)
        val observer = flatMapUpstreamAndSubscribe(inners)
        val error = Throwable()

        source.onNext(1)
        inners[1].onError(error)

        observer.assertError(error)
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

        observer.assertNoValues()
    }

    @Test
    fun unsubscribes_from_streams_WHEN_disposed() {
        val inners = createInnerSources(2)
        val observer = flatMapUpstreamAndSubscribe(inners)
        source.onNext(0)
        source.onNext(1)

        observer.dispose()

        assertFalse(source.hasSubscribers)
        assertFalse(inners[0].hasSubscribers)
        assertFalse(inners[1].hasSubscribers)
    }

    @Test
    fun unsubscribes_from_streams_WHEN_upstream_produced_error() {
        val inners = createInnerSources(2)
        flatMapUpstreamAndSubscribe(inners)
        source.onNext(0)
        source.onNext(1)

        source.onError(Throwable())

        assertFalse(source.hasSubscribers)
        assertFalse(inners[0].hasSubscribers)
        assertFalse(inners[1].hasSubscribers)
    }

    @Test
    fun unsubscribes_from_streams_WHEN_inner_source_produced_error() {
        val inners = createInnerSources(2)
        flatMapUpstreamAndSubscribe(inners)
        source.onNext(0)
        source.onNext(1)

        inners[1].onError(Throwable())

        assertFalse(source.hasSubscribers)
        assertFalse(inners[0].hasSubscribers)
        assertFalse(inners[1].hasSubscribers)
    }

    private fun flatMapUpstreamAndSubscribe(innerSources: List<Observable<String?>>): TestObservableObserver<String?> =
        flatMapUpstreamAndSubscribe { innerSources[it!!] }

    private fun flatMapUpstreamAndSubscribe(mapper: (Int?) -> Observable<String?>): TestObservableObserver<String?> =
        source.flatMap(mapper).test()

    private fun createInnerSources(count: Int): List<TestObservable<String?>> =
        List(count) { TestObservable<String?>() }
}
