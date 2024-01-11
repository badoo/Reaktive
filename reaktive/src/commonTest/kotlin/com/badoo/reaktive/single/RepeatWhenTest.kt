package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.maybe.maybeOfEmpty
import com.badoo.reaktive.maybe.maybeUnsafe
import com.badoo.reaktive.observable.repeatWhen
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class RepeatWhenTest : SingleToObservableTests by SingleToObservableTestsImpl({ repeatWhen { _, _ -> maybeOfEmpty<Unit>() } }) {

    @Test
    fun emits_all_values_from_all_iterations_in_order_WHEN_upstream_and_handler_are_asynchronous() {
        val upstream = TestSingle<Int?>()
        val handlerMaybes = List(3) { TestMaybe<Unit>() }

        val observer = upstream.repeatWhen { repeatNumber, _ -> handlerMaybes[repeatNumber] }.test()
        upstream.onSuccess(0)
        handlerMaybes[1].onSuccess(Unit)
        upstream.onSuccess(null)
        handlerMaybes[2].onSuccess(Unit)
        upstream.onSuccess(1)

        observer.assertValues(0, null, 1)
    }

    @Test
    fun emits_all_values_from_all_observers_in_order_WHEN_upstream_and_handler_are_synchronous() {
        var number = 0
        val upstream =
            singleUnsafe<Int?> { observer ->
                observer.onSubscribe(Disposable())
                observer.onSuccess(++number)
            }

        val observer =
            upstream
                .repeatWhen { repeatNumber, _ ->
                    maybeUnsafe<Unit> { observer ->
                        observer.onSubscribe(Disposable())
                        if (repeatNumber < 3) {
                            observer.onSuccess(Unit)
                        } else {
                            observer.onComplete()
                        }
                    }
                }
                .test()

        observer.assertValues(1, 2, 3)
    }

    @Test
    fun does_not_subscribe_to_upstream_recursively() {
        var subscribeCounter = 0
        var maxSubscribers = 0
        val upstream =
            singleUnsafe<Int?> { observer ->
                subscribeCounter++
                maxSubscribers = max(maxSubscribers, subscribeCounter)
                observer.onSubscribe(Disposable())
                observer.onSuccess(0)
                subscribeCounter--
            }

        upstream
            .repeatWhen { repeatNumber, _ ->
                maybeUnsafe<Unit> { observer ->
                    observer.onSubscribe(Disposable())
                    if (repeatNumber == 1) {
                        observer.onSuccess(Unit)
                    } else {
                        observer.onComplete()
                    }
                }
            }
            .test()

        assertEquals(1, maxSubscribers)
    }

    @Test
    fun calls_handler_with_upstream_values() {
        val upstream = TestSingle<Int?>()
        val handlerMaybe = TestMaybe<Unit>()
        val values = ArrayList<Int?>()

        upstream
            .repeatWhen { _, value ->
                values += value
                handlerMaybe
            }
            .test()

        upstream.onSuccess(0)
        handlerMaybe.onSuccess(Unit)
        upstream.onSuccess(null)
        handlerMaybe.onSuccess(Unit)
        upstream.onSuccess(1)

        assertEquals(listOf(0, null, 1), values)
    }

    @Test
    fun completes_WHEN_handler_completed_after_first_iteration() {
        val upstream = TestSingle<Int?>()
        val handlerMaybe = TestMaybe<Unit>()

        val observer = upstream.repeatWhen { _, _ -> handlerMaybe }.test()
        upstream.onSuccess(0)
        handlerMaybe.onComplete()

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_handler_completed_after_second_iteration() {
        val upstream = TestSingle<Int?>()
        val handlerMaybe = TestMaybe<Unit>()

        val observer = upstream.repeatWhen { _, _ -> handlerMaybe }.test()
        upstream.onSuccess(0)
        handlerMaybe.onSuccess(Unit)
        upstream.onSuccess(1)
        handlerMaybe.onComplete()

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_handler_produced_error_after_first_iteration() {
        val upstream = TestSingle<Int?>()
        val handlerMaybe = TestMaybe<Unit>()
        val error = Exception()

        val observer = upstream.repeatWhen { _, _ -> handlerMaybe }.test()
        upstream.onSuccess(0)
        handlerMaybe.onError(error)

        observer.assertError(error)
    }

    @Test
    fun produces_error_WHEN_handler_produced_error_after_second_iteration() {
        val upstream = TestSingle<Int?>()
        val handlerMaybe = TestMaybe<Unit>()
        val error = Exception()

        val observer = upstream.repeatWhen { _, _ -> handlerMaybe }.test()
        upstream.onSuccess(0)
        handlerMaybe.onSuccess(Unit)
        upstream.onSuccess(1)
        handlerMaybe.onError(error)

        observer.assertError(error)
    }

    @Test
    fun produces_error_WHEN_second_iteration_upstream_produced_error() {
        val upstream = TestSingle<Int?>()
        val error = Exception()

        val observer = upstream.repeatWhen { _, _ -> maybeOf(Unit) }.test()
        upstream.onSuccess(0)
        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun predicate_receives_valid_attempt_WHEN_upstream_completes() {
        val upstream = TestObservable<Int?>()
        var attemptVar = 0

        upstream
            .repeatWhen { attempt ->
                attemptVar = attempt
                maybeOf(Unit)
            }
            .test()

        upstream.onComplete()
        assertEquals(attemptVar, 1)
        upstream.onComplete()
        assertEquals(attemptVar, 2)
    }
}
