package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.maybe.maybeOfEmpty
import com.badoo.reaktive.maybe.maybeUnsafe
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.plusAssign
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val number = AtomicInt()
        val upstream =
            singleUnsafe<Int?> { observer ->
                observer.onSubscribe(Disposable())
                observer.onSuccess(number.addAndGet(1))
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
        val subscribeCounter = AtomicInt()
        val maxSubscribers = AtomicInt()
        val upstream =
            singleUnsafe<Int?> { observer ->
                subscribeCounter.addAndGet(1)
                maxSubscribers.value = max(maxSubscribers.value, subscribeCounter.value)
                observer.onSubscribe(Disposable())
                observer.onSuccess(0)
                subscribeCounter.addAndGet(-1)
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

        assertEquals(1, maxSubscribers.value)
    }

    @Test
    fun calls_handler_with_upstream_values() {
        val upstream = TestSingle<Int?>()
        val handlerMaybe = TestMaybe<Unit>()
        val values = atomicList<Int?>()

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

        assertEquals(listOf(0, null, 1), values.value)
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
}
