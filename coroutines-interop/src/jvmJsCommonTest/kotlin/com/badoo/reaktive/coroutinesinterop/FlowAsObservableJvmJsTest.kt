package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlin.test.Test
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class FlowAsObservableJvmJsTest {

    @Test
    fun produces_values_in_correct_order_WHEN_flow_produced_values() {
        val observer =
            flowOf(0, null, 1, null, 2)
                .asObservable()
                .test()

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun completes_WHEN_flow_completed() {
        val observer =
            flowOf<Nothing>()
                .asObservable()
                .test()

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_flow_produced_error() {
        val error = Exception()

        val observer =
            flow<Nothing> { throw error }
                .asObservable()
                .test()

        observer.assertError(error)
    }

    @Test
    fun cancels_flow_WHEN_disposable_is_disposed() {
        lateinit var producerScope: ProducerScope<Nothing>

        val observer =
            channelFlow<Nothing> {
                producerScope = this
            }
                .asObservable()
                .test()

        observer.dispose()

        assertTrue(producerScope.isClosedForSend)
    }
}
