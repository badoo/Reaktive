package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotDisposed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RepeatUntilTest : SingleToObservableTests by SingleToObservableTestsImpl({ repeatUntil { true } }) {
    private val upstream = TestSingle<Int?>()

    @Test
    fun produces_error_WHEN_second_iteration_produced_error() {
        val observer = upstream.repeatUntil { false }.test()
        val error = Exception()

        upstream.onSuccess(1)
        upstream.onError(error)
        upstream.onSuccess(2)

        observer.assertValues(1)
        observer.assertError(error)
    }

    @Test
    fun resubscribes_to_upstream_WHEN_upstream_completed_and_predicate_is_false() {
        val upstreams = List(2) { TestSingle<Int>() }
        var index = -1

        val upstream =
            singleUnsafe { observer ->
                upstreams[++index].subscribe(observer)
            }

        upstream.repeatUntil { index == 1 }.test()

        upstreams[0].onSuccess(1)
        assertFalse(upstreams[0].hasSubscribers)
        assertTrue(upstreams[1].hasSubscribers)
    }

    @Test
    fun completes_after_first_emission_WHEN_predicate_changes_to_true() {
        val observer = upstream.repeatUntil { it != null && it == 1 }.test()

        upstream.onSuccess(1)
        upstream.onSuccess(2)

        observer.assertValues(1)
        observer.assertComplete()
    }

    @Test
    fun does_not_completes_after_second_emission_WHEN_predicate_is_still_false() {
        val observer = upstream.repeatUntil { it != null && it > 2 }.test()

        upstream.onSuccess(1)
        upstream.onSuccess(2)

        observer.assertValues(1, 2)
        observer.assertNotComplete()
    }

    @Test
    fun does_not_resubscribe_to_upstream_WHEN_disposed_and_upstream_completed_predicate_is_false() {
        var isResubscribed = false
        var upstreamObserver: SingleObserver<Int>? = null

        val upstream =
            singleUnsafe { observer ->
                if (upstreamObserver == null) {
                    observer.onSubscribe(Disposable())
                    upstreamObserver = observer
                } else {
                    isResubscribed = true
                }
            }

        val downstreamObserver = upstream.repeatUntil { it == 1 }.test()

        downstreamObserver.dispose()
        requireNotNull(upstreamObserver).onSuccess(0)

        assertFalse(isResubscribed)
    }

    @Test
    fun does_not_resubscribe_to_upstream_recursively_predicate_is_false() {
        var isFirstIteration = true
        var isFirstIterationFinished = false
        var isSecondIterationRecursive = false

        val upstream =
            singleUnsafe { observer ->
                if (isFirstIteration) {
                    isFirstIteration = false
                    observer.onSubscribe(Disposable())
                    observer.onSuccess(1)
                    isFirstIterationFinished = true
                } else {
                    isSecondIterationRecursive = !isFirstIterationFinished
                }
            }

        upstream.repeatUntil { it == 1 }.test()

        assertFalse(isSecondIterationRecursive)
    }

    @Test
    fun emits_only_first_value_WHEN_predicate_is_always_true() {
        val observer = upstream.repeatUntil { true }.test()

        upstream.onSuccess(1)
        upstream.onSuccess(2)
        upstream.onSuccess(null)
        upstream.onSuccess(4)

        observer.assertValues(1)
    }

    @Test
    fun do_not_complete_WHEN_predicate_is_always_false() {
        val observer = upstream.repeatUntil { false }.test()

        upstream.onSuccess(0)
        observer.assertValues(0)
        observer.assertNotComplete()
    }

    @Test
    fun emits_all_values_of_WHEN_predicate_is_always_false() {
        val observer = upstream.repeatUntil { false }.test()

        upstream.onSuccess(0)
        upstream.onSuccess(null)
        upstream.onSuccess(2)
        upstream.onSuccess(3)

        observer.assertValues(0, null, 2, 3)
    }

    @Test
    fun emits_all_values_of_WHEN_till_two_is_emitted() {
        val observer = upstream.repeatUntil { it != null && it >= 2 }.test()

        upstream.onSuccess(0)
        upstream.onSuccess(null)
        upstream.onSuccess(2)
        upstream.onSuccess(3)

        observer.assertValues(0, null, 2)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_predicate_throw_exception() {
        val error = Exception()
        val observer = upstream.repeatUntil { throw error }.test()
        upstream.onSuccess(0)
        observer.assertError(error)
    }

    @Test
    fun do_not_disposes_downstream_disposable_WHEN_upstream_succeeded_predicate_is_false() {
        val observer = upstream.repeatUntil { false }.test()
        upstream.onSuccess(0)
        observer.assertNotDisposed()
    }
}
