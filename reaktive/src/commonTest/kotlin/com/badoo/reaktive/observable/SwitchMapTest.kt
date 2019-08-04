package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SwitchMapTest :
    ObservableToObservableTests by ObservableToObservableTests<Unit>({ switchMap { TestObservable<Int>() } }) {

    private val source = TestObservable<Int>()

    @Test
    fun subscribes_to_upstream() {
        switchMapUpstreamAndSubscribe { TestObservable() }

        assertTrue(source.hasSubscribers)
    }

    @Test
    fun subscribes_to_latest_inner_source() {
        val inners = createInnerSources(2)
        switchMapUpstreamAndSubscribe(inners)

        source.onNext(0)
        assertTrue(inners[0].hasSubscribers)

        source.onNext(1)
        assertFalse(inners[0].hasSubscribers)
        assertTrue(inners[1].hasSubscribers)
    }

    @Test
    fun produces_values_in_correct_order() {
        val inners = createInnerSources(3)
        val observer = switchMapUpstreamAndSubscribe(inners)

        source.onNext(0)
        inners[0].onNext("0a")
        source.onNext(1)
        inners[0].onNext("0b") // Should be ignored
        inners[1].onNext("1a")
        inners[0].onComplete() // Should be ignored
        source.onNext(2)
        inners[1].onNext(null) // Should be ignored
        inners[1].onComplete() // Should be ignored
        inners[2].onNext(null)
        inners[2].onNext("2a")
        source.onComplete()
        inners[2].onNext("2b")
        inners[2].onComplete()

        observer.assertValues("0a", "1a", null, "2a", "2b")
    }

    @Test
    fun completes_WHEN_upstream_completed_without_values() {
        val observer = switchMapUpstreamAndSubscribe { TestObservable() }

        source.onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed() {
        val observer = switchMapUpstreamAndSubscribe { TestObservable() }

        source.onNext(0)
        source.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun completes_WHEN_upstream_produced_values_and_completed_and_latest_inner_source_is_completed() {
        val inners = createInnerSources(3)
        val observer = switchMapUpstreamAndSubscribe(inners)

        source.onNext(0)
        source.onNext(1)
        source.onNext(2)
        source.onComplete()
        inners[2].onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed_and_latest_inner_source_is_not_completed() {
        val inners = createInnerSources(3)
        val observer = switchMapUpstreamAndSubscribe(inners)

        source.onNext(0)
        inners[0].onComplete()
        source.onNext(1)
        source.onNext(2)
        source.onComplete()
        inners[1].onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun produces_error_WHEN_inner_source_produced_error() {
        val inners = createInnerSources(2)
        val observer = switchMapUpstreamAndSubscribe(inners)
        val error = Throwable()

        source.onNext(0)
        inners[0].onNext("0a")
        inners[0].onError(error)
        source.onNext(1)
        inners[1].onNext("1a")

        observer.assertValue("0a")
        observer.assertError(error)
    }

    @Test
    fun does_not_produce_more_values_WHEN_disposed() {
        val inner = TestObservable<String>()
        val observer = switchMapUpstreamAndSubscribe { inner }
        source.onNext(0)
        inner.onNext("a")
        observer.reset()

        observer.dispose()
        inner.onNext("b")

        observer.assertNoValues()
    }

    @Test
    fun disposes_previous_stream_WHEN_source_emits_next_value() {
        val inners = createInnerSources(2)
        switchMapUpstreamAndSubscribe(inners)

        source.onNext(0)
        assertFalse(inners[0].isDisposed)

        source.onNext(1)
        assertFalse(source.isDisposed)
        assertTrue(inners[0].isDisposed)
        assertFalse(inners[1].isDisposed)
    }

    @Test
    fun disposes_latest_stream_WHEN_disposed() {
        val inners = createInnerSources(2)
        val observer = switchMapUpstreamAndSubscribe(inners)
        source.onNext(0)
        source.onNext(1)
        assertTrue(inners[0].isDisposed)

        observer.dispose()

        assertTrue(source.isDisposed)
        assertTrue(inners[1].isDisposed)
    }

    @Test
    fun disposes_streams_WHEN_upstream_produced_error() {
        val inners = createInnerSources(2)
        switchMapUpstreamAndSubscribe(inners)
        source.onNext(0)
        source.onNext(1)
        assertTrue(inners[0].isDisposed)

        source.onError(Throwable())

        assertTrue(source.isDisposed)
        assertTrue(inners[1].isDisposed)
    }

    @Test
    fun disposes_stream_WHEN_inner_source_produced_error() {
        val inners = createInnerSources(2)
        switchMapUpstreamAndSubscribe(inners)
        source.onNext(0)
        source.onNext(1)
        assertTrue(inners[0].isDisposed)

        inners[1].onError(Throwable())

        assertTrue(source.isDisposed)
        assertTrue(inners[1].isDisposed)
    }

    private fun switchMapUpstreamAndSubscribe(innerSources: List<Observable<String?>>): TestObservableObserver<String?> =
        switchMapUpstreamAndSubscribe { innerSources[it] }

    private fun switchMapUpstreamAndSubscribe(mapper: (Int) -> Observable<String?>): TestObservableObserver<String?> =
        source.switchMap(mapper).test()

    private fun createInnerSources(count: Int): List<TestObservable<String?>> =
        List(count) { TestObservable<String?>() }
}