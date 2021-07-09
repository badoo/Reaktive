package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.coroutinesinterop.test.CoroutineCancellationVerifier
import com.badoo.reaktive.coroutinesinterop.test.verifyCancellation
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.observable.toList
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.single.blockingGet
import com.badoo.reaktive.test.observable.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
class FlowAsObservableNativeTest {

    @Test
    fun produces_error_WHEN_flow_produced_error() {
        val error = Exception("error")

        val observable =
            flow<Nothing> { throw error }
                .asObservable()
                .toList()

        assertFailsWith<Exception>(message = "error") {
            observable.blockingGet()
        }
    }

    @Test
    fun produces_error_WHEN_launch_in_flow_produced_error() {
        val observable =
            flow<Nothing> {
                coroutineScope {
                    launch { throw Exception("Msg") }
                    yield()
                }
            }
                .asObservable()
                .toList()

        assertFailsWith<Exception>(message = "Msg") {
            observable.blockingGet()
        }
    }

    @Test
    fun produces_values_in_correct_order_and_completes_WHEN_flow_produced_values() {
        val values =
            flowOf(0, null, 1, null, 2)
                .asObservable()
                .toList()
                .blockingGet()

        assertEquals(listOf(0, null, 1, null, 2), values)
    }

    @Test
    fun cancels_flow_WHEN_disposable_is_disposed() {
        val cancellationVerifier = CoroutineCancellationVerifier()

        val observer =
            flow<Nothing> { cancellationVerifier.suspendCancellable() }
                .asObservable()
                .subscribeOn(ioScheduler)
                .test()

        cancellationVerifier.verifyCancellation(observer)
    }
}
