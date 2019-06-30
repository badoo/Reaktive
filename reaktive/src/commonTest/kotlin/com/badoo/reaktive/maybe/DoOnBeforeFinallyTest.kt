package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.utils.SafeMutableList
import com.badoo.reaktive.utils.atomic.AtomicInt
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeFinallyTest
    : MaybeToMaybeTests by MaybeToMaybeTests<Unit>({ doOnBeforeFinally {} }) {

    private val upstream = TestMaybe<Nothing>()

    @Test
    fun calls_action_before_completion() {
        val callOrder = SafeMutableList<String>()

        upstream
            .doOnBeforeFinally {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultMaybeObserver<Nothing> {
                    override fun onComplete() {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onComplete()

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
                object : DefaultMaybeObserver<Nothing> {
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

        maybeUnsafe<Unit> { observer ->
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
    fun does_not_call_action_second_time_WHEN_downstream_disposed_and_upstream_completed() {
        val count = AtomicInt()

        upstream
            .doOnBeforeFinally {
                count.incrementAndGet(1)
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
                count.incrementAndGet(1)
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
                    count.incrementAndGet(1)
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
                    count.incrementAndGet(1)
                }
                .test()

        upstream.onError(Throwable())
        observer.dispose()

        assertEquals(1, count.value)
    }
}