package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.observable.observableUnsafe
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.onNext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class ObservableAsFlowTest {

    private val upstream = TestObservable<Int?>(autoFreeze = false)

    @Test
    fun produces_values_in_correct_order_WHEN_upstream_produced_values() {
        val values = listOf(0, null, 1, null, 2)
        val list = ArrayList<Int?>()

        GlobalScope.launch(Dispatchers.Unconfined) {
            upstream.asFlow().collect {
                list.add(it)
            }
        }

        upstream.onNext(*values.toTypedArray())

        assertEquals(values, list)
    }

    @Test
    fun completes_WHEN_upstream_completed() {
        var isCompleted = false
        GlobalScope.launch(Dispatchers.Unconfined) {
            upstream.asFlow().collect {}
            isCompleted = true
        }

        upstream.onComplete()

        assertTrue(isCompleted)
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error() {
        var isError = false

        GlobalScope.launch(Dispatchers.Unconfined) {
            try {
                upstream.asFlow().collect {}
            } catch (e: Throwable) {
                isError = true
            }
        }

        upstream.onError(Exception())

        assertTrue(isError)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_flow_is_cancelled() {
        val scope = CoroutineScope(Dispatchers.Unconfined)

        scope.launch(Dispatchers.Unconfined) {
            upstream.asFlow().collect {}
        }

        scope.cancel()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun throws_exception_WHEN_upstream_subscribe_throws_exception() {
        var isThrown = false

        GlobalScope.launch(Dispatchers.Unconfined) {
            observableUnsafe<Nothing> { throw Exception() }
                .asFlow()
                .run {
                    try {
                        collect()
                    } catch (e: Exception) {
                        isThrown = true
                    }
                }
        }

        assertTrue(isThrown)
    }
}
