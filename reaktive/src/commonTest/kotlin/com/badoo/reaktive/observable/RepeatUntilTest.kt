package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RepeatUntilTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ repeatUntil { true } }) {
    @Test
    fun resubscribes_on_complete_till_predicate_is_true() {
        var count = 0
        val upstream = TestObservable<Int?>()

        val observer = upstream.repeatUntil { count >= 2 }.test()

        upstream.onNext(++count, ++count)
        observer.assertValues(1, 2)
    }

    @Test
    fun produces_error_WHEN_second_iteration_produced_error() {
        val upstream = TestObservable<Int>()
        val observer = upstream.repeatUntil { false }.test()
        val error = Exception()

        upstream.onNext(1)
        upstream.onError(error)
        upstream.onNext(2)

        observer.assertValues(1)
        observer.assertError(error)
    }

    @Test
    fun resubscribes_to_upstream_WHEN_upstream_completed_and_predicate_is_false() {
        val upstreams = List(2) { TestObservable<Int>() }
        var index = -1

        val upstream =
            observableUnsafe { observer ->
                upstreams[++index].subscribe(observer)
            }

        upstream.repeatUntil { index == 1 }.test()

        upstreams[0].onComplete()
        assertFalse(upstreams[0].hasSubscribers)
        assertTrue(upstreams[1].hasSubscribers)
    }

    @Test
    fun resubscribes_on_complete_multiple_emissions_predicate_is_false() {
        var count = 0
        val upstream = observable {
            for (i in 1..10) {
                it.onNext(++count)
            }
            it.onComplete()
        }

        upstream.repeatUntil { count > 10 }.test()

        assertEquals(20, count)
    }

    @Test
    fun completes_after_second_iteration_WHEN_predicate_changes_to_true() {
        var count = 0
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeatUntil { count > 1 }.test()

        upstream.onNext(++count)
        upstream.onComplete()
        upstream.onNext(++count)
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_completes_after_second_iteration_WHEN_predicate_is_still_false() {
        var count = 0
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeatUntil { count > 2 }.test()

        upstream.onNext(++count)
        upstream.onComplete()
        upstream.onNext(++count)
        upstream.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun does_not_resubscribe_to_upstream_WHEN_disposed_and_upstream_completed_predicate_is_false() {
        var count = 0
        var isResubscribed = false
        var upstreamObserver: ObservableObserver<Int>? = null

        val upstream =
            observableUnsafe<Int> { observer ->
                if (upstreamObserver == null) {
                    observer.onSubscribe(Disposable())
                    upstreamObserver = observer
                } else {
                    isResubscribed = true
                }
            }

        val downstreamObserver = upstream.repeatUntil { count == 2 }.test()

        downstreamObserver.dispose()
        requireNotNull(upstreamObserver).onNext(++count)
        requireNotNull(upstreamObserver).onComplete()

        assertFalse(isResubscribed)
    }

    @Test
    fun does_not_resubscribe_to_upstream_recursively_predicate_is_false() {
        var count = 0
        var isFirstIteration = true
        var isFirstIterationFinished = false
        var isSecondIterationRecursive = false

        val upstream =
            observableUnsafe<Int> { observer ->
                if (isFirstIteration) {
                    isFirstIteration = false
                    observer.onSubscribe(Disposable())
                    observer.onComplete()
                    count++
                    isFirstIterationFinished = true
                } else {
                    isSecondIterationRecursive = !isFirstIterationFinished
                }
            }

        upstream.repeatUntil { count == 1 }.test()

        assertFalse(isSecondIterationRecursive)
    }

    @Test
    fun emits_all_values_of_first_iteration_WHEN_predicate_is_true() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeatUntil { true }.test()

        upstream.onNext(0, null, 2)

        observer.assertValues(0, null, 2)
    }

    @Test
    fun emits_all_values_of_first_iteration_WHEN_predicate_is_always_false() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeatUntil { false }.test()

        upstream.onNext(0, null, 2)

        observer.assertValues(0, null, 2)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_predicate_throw_exception() {
        val upstream = TestObservable<Int?>()
        val error = Exception()
        val observer = upstream.repeatUntil { throw error }.test()
        upstream.onNext(0, 1, 2)
        upstream.onComplete()
        observer.assertError(error)
    }
}
