package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlin.test.Test
import kotlin.test.assertTrue

class FlowAsObservableTest {

    private val channel = BroadcastChannel<Int?>(Channel.BUFFERED)
    private val observer = channel.asFlow().asObservable().test()

    @Test
    fun produces_values_in_correct_order_WHEN_flow_produced_values() {
        channel.offer(0)
        channel.offer(null)
        channel.offer(1)
        channel.offer(null)
        channel.offer(2)

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun completes_WHEN_flow_completed() {
        channel.close()

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_flow_produced_error() {
        val error = Exception()

        channel.close(error)

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