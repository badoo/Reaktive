package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.TestCompletableObserver
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.assertNotComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.onNext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlatMapCompletableTest
    : ObservableToCompletableTests by ObservableToCompletableTestsImpl({ flatMapCompletable { TestCompletable() } }) {

    private val upstream = TestObservable<Int?>()

    @Test
    fun subscribes_to_upstream() {
        flatMapUpstreamAndSubscribe { TestCompletable() }

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
    fun completes_WHEN_upstream_completed_without_values() {
        val observer = flatMapUpstreamAndSubscribe { TestCompletable() }

        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed() {
        val observer = flatMapUpstreamAndSubscribe { TestCompletable() }

        upstream.onNext(0)
        upstream.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun completes_WHEN_upstream_produced_values_and_completed_and_all_inner_sources_are_completed() {
        val inners = createInnerSources(3)
        val observer = flatMapUpstreamAndSubscribe(inners)

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
        val observer = flatMapUpstreamAndSubscribe(inners)

        upstream.onNext(0)
        inners[0].onComplete()
        upstream.onNext(1, 2)
        upstream.onComplete()
        inners[2].onComplete()

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

    private fun flatMapUpstreamAndSubscribe(innerSources: List<Completable>): TestCompletableObserver =
        flatMapUpstreamAndSubscribe { innerSources[it!!] }

    private fun flatMapUpstreamAndSubscribe(mapper: (Int?) -> Completable): TestCompletableObserver =
        upstream.flatMapCompletable(mapper).test()

    private fun createInnerSources(count: Int): List<TestCompletable> =
        List(count) { TestCompletable() }
}
