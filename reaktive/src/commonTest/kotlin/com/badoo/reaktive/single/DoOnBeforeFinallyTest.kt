package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.utils.SafeMutableList
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.update
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeFinallyTest
    : SingleToSingleTests by SingleToSingleTests<Unit>({ doOnBeforeFinally {} }) {

    private val upstream = TestSingle<Int>()

    @Test
    fun calls_action_before_success() {
        val callOrder = SafeMutableList<String>()

        upstream
            .doOnBeforeFinally {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultSingleObserver<Int> {
                    override fun onSuccess(value: Int) {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onSuccess(0)

        assertEquals(listOf("action", "onComplete"), callOrder.items)
    }

    @Test
    fun calls_action_before_failing() {
        val callOrder = SafeMutableList<String>()
        val exception = Exception()

        upstream
            .doOnBeforeFinally {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultSingleObserver<Int> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError"
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action", "onError"), callOrder.items)
    }

    @Test
    fun calls_action_before_disposing_upstream() {
        val callOrder = SafeMutableList<String>()

        singleUnsafe<Unit> { observer ->
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

        assertEquals(listOf("action", "dispose"), callOrder.items)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_downstream_disposed_and_upstream_succeded() {
        val count = AtomicReference(0)

        upstream
            .doOnBeforeFinally {
                count.update(Int::inc)
            }
            .test()
            .dispose()

        upstream.onSuccess(0)

        assertEquals(1, count.value)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_downstream_disposed_and_upstream_produced_error() {
        val count = AtomicReference(0)

        upstream
            .doOnBeforeFinally {
                count.update(Int::inc)
            }
            .test()
            .dispose()

        upstream.onError(Throwable())

        assertEquals(1, count.value)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_upstream_succeeded_and_downstream_disposed() {
        val count = AtomicReference(0)

        val observer =
            upstream
                .doOnBeforeFinally {
                    count.update(Int::inc)
                }
                .test()

        upstream.onSuccess(0)
        observer.dispose()

        assertEquals(1, count.value)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_upstream_produced_error_and_downstream_disposed() {
        val count = AtomicReference(0)

        val observer =
            upstream
                .doOnBeforeFinally {
                    count.update(Int::inc)
                }
                .test()

        upstream.onError(Throwable())
        observer.dispose()

        assertEquals(1, count.value)
    }
}