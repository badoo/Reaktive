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
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RepeatTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ repeat(count = 0) }) {

    @Test
    fun emits_all_values_of_first_iteration_WHEN_count_is_positive() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(count = 2).test()

        upstream.onNext(0, null, 2)

        observer.assertValues(0, null, 2)
    }

    @Test
    fun emits_all_values_of_first_iteration_WHEN_count_is_0() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(count = 0).test()

        upstream.onNext(0, null, 2)

        observer.assertValues(0, null, 2)
    }

    @Test
    fun emits_all_values_of_first_iteration_WHEN_count_is_negative() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(count = -1).test()

        upstream.onNext(0, null, 2)

        observer.assertValues(0, null, 2)
    }

    @Test
    fun resubscribes_to_upstream_WHEN_upstream_completed_and_count_not_reached() {
        val upstreams = List(2) { TestObservable<Int>() }
        val index = AtomicInt(-1)

        val upstream =
            observableUnsafe<Int> { observer ->
                upstreams[index.addAndGet(1)].subscribe(observer)
            }

        upstream.repeat(count = 1).test()

        upstreams[0].onComplete()

        assertTrue(upstreams[1].hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_upstream_WHEN_upstream_completed_and_count_is_0() {
        val upstream = TestObservable<Int?>()
        upstream.repeat(count = 0).test()

        upstream.onNext(0)
        upstream.onComplete()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_upstream_WHEN_upstream_completed_and_count_is_reached() {
        val upstream = TestObservable<Int?>()
        upstream.repeat(count = 1).test()

        upstream.onComplete()
        upstream.onNext(0)
        upstream.onComplete()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun emits_all_values_of_second_iteration_WHEN_count_is_negative() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(count = -1).test()

        upstream.onNext(0, 1)
        upstream.onComplete()
        observer.reset()
        upstream.onNext(2, 3, 4)

        observer.assertValues(2, 3, 4)
    }

    @Test
    fun emits_all_values_of_second_iteration_WHEN_count_is_1() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(count = 1).test()

        upstream.onNext(0, 1)
        observer.reset()
        upstream.onComplete()
        upstream.onNext(2, 3, 4)

        observer.assertValues(2, 3, 4)
    }

    @Test
    fun completes_after_second_iteration_WHEN_count_is_1() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(count = 1).test()

        upstream.onNext(0)
        upstream.onComplete()
        upstream.onNext(1)
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun does_not_completes_after_second_iteration_WHEN_count_is_2() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.repeat(count = 2).test()

        upstream.onNext(0)
        upstream.onComplete()
        upstream.onNext(1)
        upstream.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun does_not_resubscribe_to_upstream_recursively() {
        val isFirstIteration = AtomicBoolean(true)
        val isFirstIterationFinished = AtomicBoolean()
        val isSecondIterationRecursive = AtomicBoolean()

        val upstream =
            observableUnsafe<Int> { observer ->
                if (isFirstIteration.value) {
                    isFirstIteration.value = false
                    observer.onSubscribe(Disposable())
                    observer.onComplete()
                    isFirstIterationFinished.value = true
                } else {
                    isSecondIterationRecursive.value = !isFirstIterationFinished.value
                }
            }

        upstream.repeat(count = 1).test()

        assertFalse(isSecondIterationRecursive.value)
    }

    @Test
    fun does_not_resubscribe_to_upstream_WHEN_disposed_and_upstream_completed() {
        val isResubscribed = AtomicBoolean()
        val upstreamObserver = AtomicReference<ObservableObserver<Int>?>(null)

        val upstream =
            observableUnsafe<Int> { observer ->
                if (upstreamObserver.value == null) {
                    observer.onSubscribe(Disposable())
                    upstreamObserver.value = observer
                } else {
                    isResubscribed.value = true
                }
            }

        val downstreamObserver = upstream.repeat(count = 1).test()

        downstreamObserver.dispose()
        upstreamObserver.value!!.onComplete()

        assertFalse(isResubscribed.value)
    }

    @Test
    fun emits_all_values_repeatedly_for_first_1000_iterations_WHEN_count_is_negative() {
        val list = SharedList<Int>(3000)

        observableUnsafe<Int> { observer ->
            observer.onSubscribe(Disposable())
            observer.onNext(0)
            observer.onNext(1)
            observer.onNext(2)
            observer.onComplete()
        }
            .repeat(count = -1)
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
        val observer = upstream.repeat(count = 2).test()
        val error = Exception()

        upstream.onComplete()
        upstream.onError(error)

        observer.assertError(error)
    }
}
