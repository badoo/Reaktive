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

class FlatMapTest : UpstreamDownstreamGenericTests by UpstreamDownstreamGenericTests<Unit>({ flatMap { TestObservable<Int>() } }) {

    private val source = TestObservable<Int>()

    @Test
    fun subscribes_to_upstream() {
        testFlatMap { TestObservable() }

        assertTrue(source.hasSubscribers)
    }

    @Test
    fun subscribes_to_inner_sources() {
        val inners = listOf<TestObservable<String>>(TestObservable(), TestObservable())
        testFlatMap(inners::get)

        source.onNext(0)
        source.onNext(1)

        assertTrue(inners[0].hasSubscribers)
        assertTrue(inners[1].hasSubscribers)
    }

    @Test
    fun produces_values_in_correct_order() {
        val inners = listOf<TestObservable<String>>(TestObservable(), TestObservable(), TestObservable())
        val observer = testFlatMap(inners::get)

        source.onNext(0)
        inners[0].onNext("0a")
        source.onNext(1)
        inners[0].onNext("0b")
        inners[1].onNext("1a")
        source.onNext(2)
        inners[1].onNext("1b")
        inners[2].onNext("2a")
        inners[2].onNext("2b")

        assertEquals(listOf("0a", "0b", "1a", "1b", "2a", "2b"), observer.values)
    }

    @Test
    fun completes_WHEN_upstream_completed_without_values() {
        val observer = testFlatMap { TestObservable() }

        source.onComplete()

        assertTrue(observer.isCompleted)
    }

    @Test
    fun does_not_complete_WHEN_upstream_produced_values_and_completed() {
        val observer = testFlatMap { TestObservable() }

        source.onNext(0)
        source.onComplete()

        assertFalse(observer.isCompleted)
    }

    @Test
    fun completes_WHEN_upstream_produced_values_and_completed_and_all_inner_sources_are_completed() {
        val inners = listOf<TestObservable<String>>(TestObservable(), TestObservable(), TestObservable())
        val observer = testFlatMap(inners::get)

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
        val inners = listOf<TestObservable<String>>(TestObservable(), TestObservable(), TestObservable())
        val observer = testFlatMap(inners::get)

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
        val inners = listOf<TestObservable<String>>(TestObservable(), TestObservable())
        val observer = testFlatMap(inners::get)

        source.onNext(1)
        inners[1].onError(Throwable())

        assertTrue(observer.isError)
    }

    @Test
    fun does_not_produce_more_values_WHEN_disposed() {
        val inner = TestObservable<String>()
        val observer = testFlatMap { inner }
        source.onNext(0)
        inner.onNext("a")
        observer.reset()

        observer.dispose()
        inner.onNext("b")

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun disposes_streams_WHEN_disposed() {
        val inners = listOf<TestObservable<String>>(TestObservable(), TestObservable())
        val observer = testFlatMap(inners::get)
        source.onNext(0)
        source.onNext(1)

        observer.dispose()

        assertTrue(source.isDisposed)
        assertTrue(inners[0].isDisposed)
        assertTrue(inners[1].isDisposed)
    }

    @Test
    fun disposes_streams_WHEN_completed() {
        val inners = listOf<TestObservable<String>>(TestObservable(), TestObservable())
        testFlatMap(inners::get)
        source.onNext(0)
        source.onNext(1)

        source.onComplete()
        inners[0].onComplete()
        inners[1].onComplete()

        assertTrue(source.isDisposed)
        assertTrue(inners[0].isDisposed)
        assertTrue(inners[1].isDisposed)
    }

    @Test
    fun disposes_streams_WHEN_upstream_produced_error() {
        val inners = listOf<TestObservable<String>>(TestObservable(), TestObservable())
        testFlatMap(inners::get)
        source.onNext(0)
        source.onNext(1)

        source.onError(Throwable())

        assertTrue(source.isDisposed)
        assertTrue(inners[0].isDisposed)
        assertTrue(inners[1].isDisposed)
    }

    @Test
    fun disposes_streams_WHEN_inner_source_produced_error() {
        val inners = listOf<TestObservable<String>>(TestObservable(), TestObservable())
        testFlatMap(inners::get)
        source.onNext(0)
        source.onNext(1)

        inners[1].onError(Throwable())

        assertTrue(source.isDisposed)
        assertTrue(inners[0].isDisposed)
        assertTrue(inners[1].isDisposed)
    }

    private fun testFlatMap(mapper: (Int) -> Observable<String>): TestObservableObserver<String> =
        source.flatMap(mapper).test()
}