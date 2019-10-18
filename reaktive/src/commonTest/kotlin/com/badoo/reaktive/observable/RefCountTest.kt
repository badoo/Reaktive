package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RefCountTest {

    @Test
    fun does_not_connect_WHEN_not_subscribed() {
        val isConnected = AtomicBoolean()
        val upstream = testUpstream(connect = { isConnected.value = true })

        upstream.refCount(subscriberCount = 1)

        assertFalse(isConnected.value)
    }

    @Test
    fun connects_to_upstream_WHEN_subscriber_count_1_is_reached() {
        val isConnected = AtomicBoolean()
        val upstream = testUpstream(connect = { isConnected.value = true })
        val refCount = upstream.refCount(subscriberCount = 1)

        refCount.test()

        assertTrue(isConnected.value)
    }

    @Test
    fun does_not_connect_WHEN_subscriber_count_2_is_not_reached() {
        val isConnected = AtomicBoolean()
        val upstream = testUpstream(connect = { isConnected.value = true })
        val refCount = upstream.refCount(subscriberCount = 2)

        refCount.test()

        assertFalse(isConnected.value)
    }

    @Test
    fun connects_to_upstream_WHEN_subscriber_count_2_is_reached() {
        val isConnected = AtomicBoolean()
        val upstream = testUpstream(connect = { isConnected.value = true })
        val refCount = upstream.refCount(subscriberCount = 2)

        refCount.test()
        refCount.test()

        assertTrue(isConnected.value)
    }

    @Test
    fun does_not_connect_second_time_WHEN_subscriberCount_is_1_and_subscribed_second_time() {
        val isConnected = AtomicBoolean()
        val upstream = testUpstream(connect = { isConnected.value = true })
        val refCount = upstream.refCount(subscriberCount = 1)
        refCount.test()

        isConnected.value = false
        refCount.test()

        assertFalse(isConnected.value)
    }

    @Test
    fun disconnects_from_upstream_WHEN_subscriberCount_is_1_and_unsubscribed() {
        val disposable = disposable()
        val upstream = testUpstream(connect = { it?.invoke(disposable) })
        val observer = upstream.refCount(subscriberCount = 1).test()

        observer.dispose()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun disconnects_from_upstream_WHEN_subscriberCount_is_2_and_all_subscribers_unsubscribed() {
        val disposable = disposable()
        val upstream = testUpstream(connect = { it?.invoke(disposable) })
        val refCount = upstream.refCount(subscriberCount = 1)
        val observer1 = refCount.test()
        val observer2 = refCount.test()

        observer1.dispose()
        observer2.dispose()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun does_not_disconnect_from_upstream_WHEN_subscriberCount_is_2_and_not_all_subscribers_unsubscribed() {
        val disposable = disposable()
        val upstream = testUpstream(connect = { it?.invoke(disposable) })
        val refCount = upstream.refCount(subscriberCount = 1)
        val observer1 = refCount.test()
        refCount.test()

        observer1.dispose()

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun subscription_to_upstream_happens_before_connection_to_upstream() {
        val events = SharedList<String>()
        val upstream = testUpstream(connect = { events += "connect" }, subscribe = { events += "subscribe" })
        val refCount = upstream.refCount(subscriberCount = 1)

        refCount.test()

        assertEquals(listOf("subscribe", "connect"), events)
    }

    @Test
    fun subscribes_to_upstream_for_each_subscription_from_downstream() {
        val subscribeCount = AtomicInt()
        val upstream = testUpstream(subscribe = { subscribeCount.addAndGet(1) })
        val refCount = upstream.refCount()

        repeat(3) { refCount.test() }

        assertEquals(3, subscribeCount.value)
    }

    @Test
    fun unsubscribes_from_upstream_for_each_unsubscribe_by_downstream() {
        val upstreamDisposables = SharedList<Disposable>()

        val upstream =
            testUpstream(
                subscribe = { observer ->
                    val disposable = disposable()
                    upstreamDisposables += disposable
                    observer.onSubscribe(disposable)
                }
            )

        val refCount = upstream.refCount()
        val downstreamObservers = listOf(refCount.test(), refCount.test())

        downstreamObservers.forEach(TestObservableObserver<*>::dispose)

        upstreamDisposables.forEach {
            assertTrue(it.isDisposed)
        }
    }

    @Test
    fun delivers_all_values_in_original_order_to_all_subscribes() {
        val upstreamObservers = SharedList<ObservableObserver<Int?>>()
        val upstream = testUpstream(subscribe = { upstreamObservers.add(it) })
        val refCount = upstream.refCount(subscriberCount = 2)
        val downstreamObservers = listOf(refCount.test(), refCount.test())

        upstreamObservers.forEach { it.onNext(0) }
        upstreamObservers.forEach { it.onNext(null) }
        upstreamObservers.forEach { it.onNext(1) }

        downstreamObservers.forEach {
            it.assertValues(0, null, 1)
        }
    }

    @Test
    fun delivers_completion_to_all_subscribes() {
        val upstreamObservers = SharedList<ObservableObserver<Int?>>()
        val upstream = testUpstream(subscribe = { upstreamObservers.add(it) })
        val refCount = upstream.refCount(subscriberCount = 2)
        val downstreamObservers = listOf(refCount.test(), refCount.test())

        upstreamObservers.forEach(ObservableObserver<*>::onComplete)

        downstreamObservers.forEach {
            it.assertComplete()
        }
    }

    @Test
    fun delivers_error_to_all_subscribes() {
        val upstreamObservers = SharedList<ObservableObserver<Int?>>()
        val upstream = testUpstream(subscribe = { upstreamObservers.add(it) })
        val refCount = upstream.refCount(subscriberCount = 2)
        val downstreamObservers = listOf(refCount.test(), refCount.test())
        val error = Exception()

        upstreamObservers.forEach { it.onError(error) }

        downstreamObservers.forEach {
            it.assertError(error)
        }
    }

    private fun testUpstream(
        connect: (onConnect: ((Disposable) -> Unit)?) -> Unit = {},
        subscribe: (observer: ObservableObserver<Int?>) -> Unit = {}
    ): ConnectableObservable<Int?> =
        object : ConnectableObservable<Int?> {
            override fun connect(onConnect: ((Disposable) -> Unit)?) {
                connect.invoke(onConnect)
            }

            override fun subscribe(observer: ObservableObserver<Int?>) {
                subscribe.invoke(observer)
            }
        }
}
