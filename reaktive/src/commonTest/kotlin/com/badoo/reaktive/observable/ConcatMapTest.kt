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

class ConcatMapTest
    : ObservableToObservableTests by ObservableToObservableTestsImpl({ concatMap { TestObservable<Int>() } }) {

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
        val observer = concatMapUpstreamAndSubscribe { inners[it ?: 0] }

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

        observer.assertValues("0a", "0b", "0c", "1a", null, "1b", null, "2a", null)
    }

    @Test
    fun completes_WHEN_upstream_completed_without_values() {
        val observer = concatMapUpstreamAndSubscribe { TestObservable() }

        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed() {
        val observer = concatMapUpstreamAndSubscribe { TestObservable() }

        upstream.onNext(0)
        upstream.onComplete()

        observer.assertNotComplete()
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

        observer.assertComplete()
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

        observer.assertNotComplete()
    }

    @Test
    fun produces_error_WHEN_inner_source_produced_error() {
        val inner = TestObservable<String>()
        val observer = concatMapUpstreamAndSubscribe { inner }
        val error = Throwable()

        upstream.onNext(0)
        inner.onError(error)

        observer.assertError(error)
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

        observer.assertNoValues()
    }

    @Test
    fun unsubscribes_from_source_WHEN_disposed() {
        val inner = TestObservable<String>()
        val observer = concatMapUpstreamAndSubscribe { inner }
        upstream.onNext(0)

        observer.dispose()

        assertFalse(inner.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_source_WHEN_upstream_produced_error() {
        val inner = TestObservable<String>()
        concatMapUpstreamAndSubscribe { inner }
        upstream.onNext(0)

        upstream.onError(Throwable())

        assertFalse(inner.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_source_produced_error() {
        val inner = TestObservable<String>()
        concatMapUpstreamAndSubscribe { inner }
        upstream.onNext(0)

        inner.onError(Throwable())

        assertFalse(upstream.hasSubscribers)
    }

    private fun concatMapUpstreamAndSubscribe(innerSources: List<Observable<String?>>): TestObservableObserver<String?> =
        concatMapUpstreamAndSubscribe { innerSources[it!!] }

    private fun concatMapUpstreamAndSubscribe(mapper: (Int?) -> Observable<String?>): TestObservableObserver<String?> =
        upstream.concatMap(mapper).test()

    private fun createInnerSources(count: Int): List<TestObservable<String?>> =
        List(count) { TestObservable<String?>() }
}
