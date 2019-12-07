package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlatMapMaybeTest
    : ObservableToObservableTests by ObservableToObservableTestsImpl({ flatMapMaybe { TestMaybe<Nothing>() } }) {

    private val upstream = TestObservable<Int?>()

    @Test
    fun subscribes_to_upstream() {
        flatMapUpstreamAndSubscribe { TestMaybe() }

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun subscribes_to_inner_sources() {
        val inners = createInnerSources(2)
        flatMapUpstreamAndSubscribe(inners)

        upstream.onNext(0, 1)

        assertTrue(inners[0].hasSubscribers)
        assertTrue(inners[1].hasSubscribers)
    }

    @Test
    fun produces_values_in_correct_order() {
        val inners = createInnerSources(5)
        val observer = flatMapUpstreamAndSubscribe { inners[it ?: 0] }

        upstream.onNext(null)
        inners[0].onSuccess(0)
        upstream.onNext(1, 2)
        inners[2].onSuccess(null)
        upstream.onNext(3, 4)
        inners[1].onSuccess(1)
        upstream.onComplete()
        inners[3].onComplete()
        inners[4].onSuccess(4)

        observer.assertValues(0, null, 1, 4)
    }

    @Test
    fun completes_WHEN_upstream_completed_without_values() {
        val observer = flatMapUpstreamAndSubscribe { TestMaybe() }

        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed() {
        val observer = flatMapUpstreamAndSubscribe { TestMaybe() }

        upstream.onNext(0)
        upstream.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun completes_WHEN_upstream_produced_values_and_completed_and_all_inner_sources_are_finished() {
        val inners = createInnerSources(3)
        val observer = flatMapUpstreamAndSubscribe(inners)

        upstream.onNext(0)
        inners[0].onComplete()
        upstream.onNext(1)
        inners[1].onSuccess(1)
        upstream.onNext(2)
        upstream.onComplete()
        inners[2].onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed_and_not_all_inner_sources_are_finished() {
        val inners = createInnerSources(3)
        val observer = flatMapUpstreamAndSubscribe(inners)

        upstream.onNext(0)
        inners[0].onComplete()
        upstream.onNext(1, 2)
        upstream.onComplete()
        inners[2].onSuccess(2)

        observer.assertNotComplete()
    }

    @Test
    fun produces_error_WHEN_inner_source_produced_error() {
        val inners = createInnerSources(2)
        val observer = flatMapUpstreamAndSubscribe(inners)
        val error = Throwable()

        upstream.onNext(1)
        inners[1].onError(error)

        observer.assertError(error)
    }

    @Test
    fun does_not_produce_more_values_WHEN_disposed() {
        val inners = createInnerSources(2)
        val observer = flatMapUpstreamAndSubscribe { inners[it!!] }
        upstream.onNext(0, 1)
        inners[0].onSuccess(0)
        observer.reset()

        observer.dispose()
        inners[1].onSuccess(1)

        observer.assertNoValues()
    }

    @Test
    fun unsubscribes_from_streams_WHEN_disposed() {
        val inners = createInnerSources(2)
        val observer = flatMapUpstreamAndSubscribe(inners)
        upstream.onNext(0, 1)

        observer.dispose()

        assertFalse(upstream.hasSubscribers)
        assertFalse(inners[0].hasSubscribers)
        assertFalse(inners[1].hasSubscribers)
    }

    @Test
    fun unsubscribes_from_streams_WHEN_upstream_produced_error() {
        val inners = createInnerSources(2)
        flatMapUpstreamAndSubscribe(inners)
        upstream.onNext(0, 1)

        upstream.onError(Throwable())

        assertFalse(upstream.hasSubscribers)
        assertFalse(inners[0].hasSubscribers)
        assertFalse(inners[1].hasSubscribers)
    }

    @Test
    fun unsubscribes_from_streams_WHEN_inner_source_produced_error() {
        val inners = createInnerSources(2)
        flatMapUpstreamAndSubscribe(inners)
        upstream.onNext(0, 1)

        inners[1].onError(Throwable())

        assertFalse(upstream.hasSubscribers)
        assertFalse(inners[0].hasSubscribers)
        assertFalse(inners[1].hasSubscribers)
    }

    private fun flatMapUpstreamAndSubscribe(innerSources: List<Maybe<Int?>>): TestObservableObserver<Int?> =
        flatMapUpstreamAndSubscribe { innerSources[it!!] }

    private fun flatMapUpstreamAndSubscribe(mapper: (Int?) -> Maybe<Int?>): TestObservableObserver<Int?> =
        upstream.flatMapMaybe(mapper).test()

    private fun createInnerSources(count: Int): List<TestMaybe<Int?>> =
        List(count) { TestMaybe<Int?>() }
}
