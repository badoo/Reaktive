package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
import kotlin.test.Test
import kotlin.test.assertFalse

@ExperimentalCoroutinesApi
class FlowAsObservableTest {

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
        var scope by AtomicReference<CoroutineScope?>(null)

        val observer =
            channelFlow<Nothing> {
                coroutineScope {
                    scope = this
                }
            }
                .asObservable()
                .test()

        observer.dispose()

        assertFalse(scope!!.isActive)
    }
}
