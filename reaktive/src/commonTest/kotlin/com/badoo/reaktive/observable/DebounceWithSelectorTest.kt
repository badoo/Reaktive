package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completableOfEmpty
import com.badoo.reaktive.completable.completableTimer
import com.badoo.reaktive.completable.completableUnsafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DebounceWithSelectorTest :
    ObservableToObservableTests by ObservableToObservableTests<Int>({ debounce { completableTimer(0L, TestScheduler()) } }) {

    private val upstream = TestObservable<String?>()
    private val scheduler = TestScheduler()

    @Test
    fun subscribes_to_upstream() {
        createDefaultObserver()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun emits_immediately_WHEN_there_is_no_timeout_for_item() {
        val observer = createDefaultObserver()

        upstream.onNext(null)

        observer.assertValue(null)
    }

    @Test
    fun does_not_emit_WHEN_timeout_not_reached() {
        val observer = createDefaultObserver()

        upstream.onNext("1")
        scheduler.timer.advanceBy(99L)

        observer.assertNoValues()
    }

    @Test
    fun emits_WHEN_timeout_is_reached() {
        val observer = createDefaultObserver()

        upstream.onNext("1")
        scheduler.timer.advanceBy(100L)

        observer.assertValue("1")
    }

    @Test
    fun does_not_emit_WHEN_timeout_since_last_item_not_reached() {
        val observer = createDefaultObserver()

        upstream.onNext("1")
        scheduler.timer.advanceBy(50L)
        upstream.onNext("2")
        scheduler.timer.advanceBy(99L)

        observer.assertNoValues()
    }

    @Test
    fun emits_WHEN_timeout_since_last_item_is_reached() {
        val observer = createDefaultObserver()

        upstream.onNext("1")
        scheduler.timer.advanceBy(50L)
        upstream.onNext("2")
        scheduler.timer.advanceBy(100L)

        observer.assertValue("2")
    }

    @Test
    fun emits_last_unprocessed_item_WHEN_completed() {
        val observer = createDefaultObserver()

        upstream.onNext("1")
        upstream.onNext("2")
        upstream.onComplete()

        observer.assertValue("2")
    }

    @Test
    fun does_not_emit_last_item_WHEN_already_emitted_and_completed() {
        val observer = createDefaultObserver()

        upstream.onNext("1")
        scheduler.timer.advanceBy(100L)
        observer.reset()
        upstream.onComplete()

        observer.assertNoValues()
    }

    @Test
    fun produces_values_in_correct_order() {
        val observer = createDefaultObserver()

        upstream.onNext("0a")
        scheduler.timer.advanceBy(100L)

        upstream.onNext("1a") // Should be ignored
        scheduler.timer.advanceBy(99L)
        upstream.onNext("1b")
        scheduler.timer.advanceBy(100L)

        upstream.onNext(null)
        upstream.onNext("2a")
        scheduler.timer.advanceBy(100L)

        upstream.onNext("3a") // Should be ignored
        scheduler.timer.advanceBy(99L)
        upstream.onNext(null)
        scheduler.timer.advanceBy(0L)

        upstream.onNext("4a")
        upstream.onComplete()

        observer.assertValues("0a", "1b", null, "2a", null, "4a")
    }

    @Test
    fun produces_error_WHEN_source_produced_error() {
        val error = Throwable()
        val observer = createDefaultObserver()

        upstream.onNext("0a")
        upstream.onError(error)

        observer.assertNoValues()
        observer.assertError(error)
    }

    @Test
    fun produces_value_and_error_WHEN_source_produced_error_after_timeout() {
        val error = Throwable()
        val observer = createDefaultObserver()

        upstream.onNext("0a")
        scheduler.timer.advanceBy(100L)
        upstream.onError(error)

        observer.assertValue("0a")
        observer.assertError(error)
    }

    @Test
    fun produces_error_WHEN_debounce_selector_throws_exception() {
        val error = Throwable()
        val observer = upstream.debounce { throw error }.test()

        upstream.onNext("1")

        observer.assertNoValues()
        observer.assertError(error)
    }

    @Test
    fun produces_error_WHEN_inner_source_produced_error() {
        val source = TestObservable<Int>()
        val inners = createInnerSources(2)
        val observer = source.debounce { inners[it] }.test()

        val error = Throwable()

        source.onNext(0)
        inners[0].onComplete()
        source.onNext(1)
        inners[1].onError(error)

        observer.assertValue(0)
        observer.assertError(error)
    }

    @Test
    fun does_not_produce_more_values_WHEN_disposed() {
        val observer = createDefaultObserver()

        upstream.onNext("1")
        scheduler.timer.advanceBy(100L)
        observer.reset()

        observer.dispose()
        upstream.onNext("2")
        scheduler.timer.advanceBy(100L)

        observer.assertNoValues()
    }

    @Test
    fun unsubscribes_from_previous_stream_WHEN_source_emits_next_value() {
        val source = TestObservable<Int>()
        val inners = createInnerSources(2)
        source.debounce { inners[it] }.test()

        source.onNext(0)
        assertTrue(inners[0].hasSubscribers)

        source.onNext(1)
        assertTrue(source.hasSubscribers)
        assertFalse(inners[0].hasSubscribers)
        assertTrue(inners[1].hasSubscribers)
    }

    @Test
    fun unsubscribes_from_latest_stream_WHEN_disposed() {
        val source = TestObservable<Int>()
        val inners = createInnerSources(2)
        val observer = source.debounce { inners[it] }.test()

        source.onNext(0)
        source.onNext(1)
        assertFalse(inners[0].hasSubscribers)

        observer.dispose()

        assertFalse(source.hasSubscribers)
        assertFalse(inners[1].hasSubscribers)
    }

    @Test
    fun unsubscribes_from_streams_WHEN_upstream_produced_error() {
        val source = TestObservable<Int>()
        val inners = createInnerSources(2)
        source.debounce { inners[it] }.test()

        source.onNext(0)
        source.onNext(1)
        assertFalse(inners[0].hasSubscribers)

        source.onError(Throwable())

        assertFalse(source.hasSubscribers)
        assertFalse(inners[1].hasSubscribers)
    }

    @Test
    fun unsubscribes_from_stream_WHEN_inner_source_produced_error() {
        val source = TestObservable<Int>()
        val inners = createInnerSources(2)
        source.debounce { inners[it] }.test()

        source.onNext(0)
        source.onNext(1)
        assertFalse(inners[0].hasSubscribers)

        inners[1].onError(Throwable())

        assertFalse(source.hasSubscribers)
        assertFalse(inners[1].hasSubscribers)
    }

    @Test
    fun disposes_previous_inner_source_disposable_IF_it_is_provided_after_new_source_disposable() {
        val source = TestObservable<Int>()

        val innerObserver1 = AtomicReference<CompletableObserver?>(null)
        val inner1 = completableUnsafe { observer -> innerObserver1.value = observer }
        val innerDisposable1 = Disposable()

        val innerObserver2 = AtomicReference<CompletableObserver?>(null)
        val inner2 = completableUnsafe { observer -> innerObserver2.value = observer }

        val inners = listOf(inner1, inner2)
        source.debounce { inners[it] }.test()

        source.onNext(0)
        source.onNext(1)
        innerObserver2.value!!.onSubscribe(Disposable())
        innerObserver1.value!!.onSubscribe(innerDisposable1)

        assertTrue(innerDisposable1.isDisposed)
    }

    @Test
    fun does_not_dispose_new_inner_source_disposable_WHEN_previous_inner_source_disposable_is_provided_after_new_one() {
        val source = TestObservable<Int>()

        val innerObserver1 = AtomicReference<CompletableObserver?>(null)
        val inner1 = completableUnsafe { observer -> innerObserver1.value = observer }

        val innerObserver2 = AtomicReference<CompletableObserver?>(null)
        val inner2 = completableUnsafe { observer -> innerObserver2.value = observer }
        val innerDisposable2 = Disposable()

        val inners = listOf(inner1, inner2)
        source.debounce { inners[it] }.test()

        source.onNext(0)
        source.onNext(1)
        innerObserver2.value!!.onSubscribe(innerDisposable2)
        innerObserver1.value!!.onSubscribe(Disposable())

        assertFalse(innerDisposable2.isDisposed)
    }

    private fun createDefaultObserver(): TestObservableObserver<String?> = upstream.debounce { value ->
        if (value.isNullOrEmpty()) completableOfEmpty() else completableTimer(100L, scheduler)
    }.test()

    private fun createInnerSources(count: Int): List<TestCompletable> =
        List(count) { TestCompletable() }

}
