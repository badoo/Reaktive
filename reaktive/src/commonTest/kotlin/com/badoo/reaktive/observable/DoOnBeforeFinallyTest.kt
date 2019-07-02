package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.plusAssign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeFinallyTest
    : ObservableToObservableTests by ObservableToObservableTests<Unit>({ doOnBeforeFinally {} }) {

    private val upstream = TestObservable<Int>()

    @Test
    fun calls_action_before_completion() {
        val callOrder = atomicList<String>()

        upstream
            .doOnBeforeFinally {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultObservableObserver<Int> {
                    override fun onComplete() {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onComplete()

        assertEquals(listOf("action", "onComplete"), callOrder.value)
    }

    @Test
    fun calls_action_before_failing() {
        val callOrder = atomicList<String>()
        val exception = Exception()

        upstream
            .doOnBeforeFinally {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultObservableObserver<Int> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError"
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action", "onError"), callOrder.value)
    }

    @Test
    fun calls_action_before_disposing_upstream() {
        val callOrder = atomicList<String>()

        observableUnsafe<Unit> { observer ->
            observer.onSubscribe(
                disposable {
                    callOrder += "dispose"
                }
            )
        }
            .doOnBeforeFinally {
                callOrder += "action"
            }
            .test()
            .dispose()

        assertEquals(listOf("action", "dispose"), callOrder.value)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_downstream_disposed_and_upstream_completed() {
        val count = AtomicInt()

        upstream
            .doOnBeforeFinally {
                count.addAndGet(1)
            }
            .test()
            .dispose()

        upstream.onComplete()

        assertEquals(1, count.value)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_downstream_disposed_and_upstream_produced_error() {
        val count = AtomicInt()

        upstream
            .doOnBeforeFinally {
                count.addAndGet(1)
            }
            .test()
            .dispose()

        upstream.onError(Throwable())

        assertEquals(1, count.value)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_upstream_completed_and_downstream_disposed() {
        val count = AtomicInt()

        val observer =
            upstream
                .doOnBeforeFinally {
                    count.addAndGet(1)
                }
                .test()

        upstream.onComplete()
        observer.dispose()

        assertEquals(1, count.value)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_upstream_produced_error_and_downstream_disposed() {
        val count = AtomicInt()

        val observer =
            upstream
                .doOnBeforeFinally {
                    count.addAndGet(1)
                }
                .test()

        upstream.onError(Throwable())
        observer.dispose()

        assertEquals(1, count.value)
    }

    @Test
    fun does_not_call_action_WHEN_emitted_value() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeFinally {
                isCalled.value = true
            }
            .test()

        upstream.onNext(0)

        assertFalse(isCalled.value)
    }
}