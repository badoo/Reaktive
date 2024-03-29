package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertEquals

class PublishWithSelectorTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ publish { it } }) {

    private val upstream = TestObservable<Int?>()

    @Test
    fun subscribes_to_selected_stream_WHEN_subscribed() {
        val selected: List<TestObservable<Int?>> = listOf(TestObservable(), TestObservable())
        var index = 0
        val published = upstream.publish { selected[index++] }

        published.test()
        published.test()

        assertEquals(1, selected[0].observers.size)
        assertEquals(1, selected[1].observers.size)
    }

    @Test
    fun subscribes_to_selected_stream_before_subscribe_to_upstream() {
        val events = ArrayList<String>()
        val upstream = observableUnsafe<Nothing> { events += "upstream_subscribed" }
        val selected = observableUnsafe<Nothing> { events += "selected_subscribed" }
        val published = upstream.publish { selected }

        published.test()

        assertEquals(listOf("selected_subscribed", "upstream_subscribed"), events)
    }

    @Test
    fun subscribes_to_upstream_only_once_per_subscription() {
        val published =
            upstream.publish { inner ->
                observableUnsafe<Int?> {
                    inner.test()
                    inner.test()
                }
            }

        published.test()

        assertEquals(1, upstream.observers.size)
    }

    @Test
    fun emit_values() {
        val observer = upstream.publish { it }.test()

        upstream.onNext(0, null, 1)

        observer.assertValues(0, null, 1)
    }
}
