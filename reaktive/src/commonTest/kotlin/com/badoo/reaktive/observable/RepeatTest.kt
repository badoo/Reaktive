package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
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

class RepeatTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ repeat(times = 1) }) {

    @Test
    fun does_not_subscribe_to_upstream_WHEN_times_is_0() {
        val upstream = TestObservable<Int?>()

        upstream.repeat(times = 0).test()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun completes_WHEN_times_is_0() {
        val upstream = TestObservable<Int?>()

        val observer = upstream.repeat(times = 0).test()

        observer.assertComplete()
    }

    @Test
    fun emits_all_values_of_first_iteration_WHEN_times_is_1() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(times = 1).test()

        upstream.onNext(0, null, 2)

        observer.assertValues(0, null, 2)
    }

    @Test
    fun emits_all_values_of_first_iteration_WHEN_times_is_MAX_VALUE() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(times = Long.MAX_VALUE).test()

        upstream.onNext(0, null, 2)

        observer.assertValues(0, null, 2)
    }

    @Test
    fun resubscribes_to_upstream_WHEN_upstream_completed_and_times_not_reached() {
        val upstreams = List(2) { TestObservable<Int>() }
        var index = 0

        val upstream =
            observableUnsafe { observer ->
                upstreams[index++].subscribe(observer)
            }

        upstream.repeat(times = 2).test()

        upstreams[0].onComplete()

        assertTrue(upstreams[1].hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_upstream_WHEN_upstream_completed_and_times_is_1() {
        val upstream = TestObservable<Int?>()
        upstream.repeat(times = 1).test()

        upstream.onNext(0)
        upstream.onComplete()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_upstream_WHEN_upstream_completed_and_times_is_reached() {
        val upstream = TestObservable<Int?>()
        upstream.repeat(times = 2).test()

        upstream.onComplete()
        upstream.onNext(0)
        upstream.onComplete()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun emits_all_values_of_second_iteration_WHEN_times_is_MAX_VALUE() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(times = Long.MAX_VALUE).test()

        upstream.onNext(0, 1)
        upstream.onComplete()
        observer.reset()
        upstream.onNext(2, 3, 4)

        observer.assertValues(2, 3, 4)
    }

    @Test
    fun emits_all_values_of_second_iteration_WHEN_times_is_2() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(times = 2).test()

        upstream.onNext(0, 1)
        observer.reset()
        upstream.onComplete()
        upstream.onNext(2, 3, 4)

        observer.assertValues(2, 3, 4)
    }

    @Test
    fun completes_after_second_iteration_WHEN_times_is_2() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(times = 2).test()

        upstream.onNext(0)
        upstream.onComplete()
        upstream.onNext(1)
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_completes_after_second_iteration_WHEN_times_is_3() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(times = 3).test()

        upstream.onNext(0)
        upstream.onComplete()
        upstream.onNext(1)
        upstream.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun does_not_resubscribe_to_upstream_recursively() {
        var isFirstIteration = true
        var isFirstIterationFinished = false
        var isSecondIterationRecursive = false

        val upstream =
            observableUnsafe<Int> { observer ->
                if (isFirstIteration) {
                    isFirstIteration = false
                    observer.onSubscribe(Disposable())
                    observer.onComplete()
                    isFirstIterationFinished = true
                } else {
                    isSecondIterationRecursive = !isFirstIterationFinished
                }
            }

        upstream.repeat(times = 2).test()

        assertFalse(isSecondIterationRecursive)
    }

    @Test
    fun does_not_resubscribe_to_upstream_WHEN_disposed_and_upstream_completed() {
        var isResubscribed = false
        var upstreamObserver: ObservableObserver<Int>? = null

        val upstream =
            observableUnsafe { observer ->
                if (upstreamObserver == null) {
                    observer.onSubscribe(Disposable())
                    upstreamObserver = observer
                } else {
                    isResubscribed = true
                }
            }

        val downstreamObserver = upstream.repeat(times = 2).test()

        downstreamObserver.dispose()
        requireNotNull(upstreamObserver).onComplete()

        assertFalse(isResubscribed)
    }

    @Test
    fun emits_all_values_repeatedly_for_first_1000_iterations_WHEN_times_is_MAX_VALUE() {
        val list = ArrayList<Int>(3000)

        observableUnsafe<Int> { observer ->
            observer.onSubscribe(Disposable())
            observer.onNext(0)
            observer.onNext(1)
            observer.onNext(2)
            observer.onComplete()
        }
            .repeat(times = Long.MAX_VALUE)
            .subscribe(
                object : SerialDisposable(), ObservableObserver<Int> {
                    override fun onSubscribe(disposable: Disposable) {
                        set(disposable)
                    }

                    override fun onNext(value: Int) {
                        list += value
                        if (list.size >= 3000) {
                            dispose()
                        }
                    }

                    override fun onComplete() {
                    }

                    override fun onError(error: Throwable) {
                        throw error
                    }
                }
            )

        assertEquals(List(3000) { it % 3 }, list)
    }

    @Test
    fun produces_error_WHEN_second_iteration_produced_error() {
        val upstream = TestObservable<Int>()
        val observer = upstream.repeat(times = 3).test()
        val error = Exception()

        upstream.onComplete()
        upstream.onError(error)

        observer.assertError(error)
    }
}
