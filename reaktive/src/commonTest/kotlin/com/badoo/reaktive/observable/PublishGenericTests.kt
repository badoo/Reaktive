package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

interface PublishGenericTests {

    @Test
    fun does_not_subscribe_to_upstream_WHEN_subscribed_and_not_connected()

    @Test
    fun does_not_subscribe_to_upstream_WHEN_subscribed_after_disconnected()

    @Test
    fun subscribes_to_upstream_WHEN_not_subscribed_and_connected()

    @Test
    fun subscribes_to_upstream_WHEN_subscribed_and_connected()

    @Test
    fun subscribes_to_upstream_WHEN_disconnected_and_subscribed_and_connected()

    @Test
    fun does_not_subscribe_to_upstream_second_time_WHEN_connected_and_subscribed()

    @Test
    fun does_not_subscribe_to_upstream_second_time_WHEN_connected_twice()

    @Test
    fun subscribes_to_upstream_second_time_WHEN_connected_after_disconnect()

    @Test
    fun multicasts_values_to_all_observers_WHEN_subscribed_before_connected()

    @Test
    fun multicasts_values_to_all_observers_WHEN_subscribed_after_connected()

    @Test
    fun multicasts_values_to_all_observers_WHEN_subscribed_connected_and_subscribed_again()

    @Test
    fun does_not_multicast_to_old_observers_WHEN_disconnected()

    @Test
    fun does_not_multicast_to_old_observers_WHEN_reconnected()

    @Test
    fun multicasts_values_to_all_new_observers_WHEN_subscribed_after_reconnected()

    @Test
    fun provides_disposable_synchronously_WHEN_connected_before_subscribed()

    @Test
    fun provides_disposable_synchronously_WHEN_connected_after_subscribed()

    @Test
    fun provides_same_disposable_WHEN_connected_second_time()

    @Test
    fun does_not_subscribe_to_upstream_WHEN_disconnected_from_callback()

    @Test
    fun provides_different_disposable_WHEN_reconnected()

    @Test
    fun unsubscribes_from_upstream_WHEN_disconnected()

    @Test
    fun does_not_complete_any_observer_WHEN_disconnected()

    @Test
    fun does_not_complete_new_observers_WHEN_reconnected_and_disconnected_from_old_again()

    @Test
    fun multicasts_values_to_new_observers_WHEN_reconnected_and_disconnected_from_old_again()

    @Test
    fun completes_all_observers_WHEN_connected_and_upstream_is_completed()

    @Test
    fun multicasts_error_to_all_observers_WHEN_connected_and_upstream_produced_error()
}

@Ignore
class PublishGenericTestsImpl(
    transform: Observable<Int?>.() -> ConnectableObservable<Int?>
) : PublishGenericTests {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestObservable<Int?>()
    private val publish = upstream.transform()

    @Test
    override fun does_not_subscribe_to_upstream_WHEN_subscribed_and_not_connected() {
        publish.test()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    override fun does_not_subscribe_to_upstream_WHEN_subscribed_after_disconnected() {
        publish.connectAndGetDisposable().dispose()
        publish.test()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    override fun subscribes_to_upstream_WHEN_not_subscribed_and_connected() {
        publish.connect()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    override fun subscribes_to_upstream_WHEN_subscribed_and_connected() {
        publish.test()
        publish.connect()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    override fun subscribes_to_upstream_WHEN_disconnected_and_subscribed_and_connected() {
        publish.connectAndGetDisposable().dispose()
        publish.test()
        publish.connect()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    override fun does_not_subscribe_to_upstream_second_time_WHEN_connected_and_subscribed() {
        publish.connect()
        publish.test()

        assertEquals(1, upstream.observers.size)
    }

    @Test
    override fun does_not_subscribe_to_upstream_second_time_WHEN_connected_twice() {
        publish.connect()
        publish.connect()

        assertEquals(1, upstream.observers.size)
    }

    @Test
    override fun subscribes_to_upstream_second_time_WHEN_connected_after_disconnect() {
        publish.connectAndGetDisposable().dispose()

        publish.connect()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    override fun multicasts_values_to_all_observers_WHEN_subscribed_before_connected() {
        val observer1 = publish.test()
        val observer2 = publish.test()
        publish.connect()

        upstream.onNext(0, null, 1, null, 2)

        observer1.assertValues(0, null, 1, null, 2)
        observer2.assertValues(0, null, 1, null, 2)
    }

    @Test
    override fun multicasts_values_to_all_observers_WHEN_subscribed_after_connected() {
        publish.connect()
        val observer1 = publish.test()
        val observer2 = publish.test()

        upstream.onNext(0, null, 1, null, 2)

        observer1.assertValues(0, null, 1, null, 2)
        observer2.assertValues(0, null, 1, null, 2)
    }

    @Test
    override fun multicasts_values_to_all_observers_WHEN_subscribed_connected_and_subscribed_again() {
        val observer1 = publish.test()
        publish.connect()
        val observer2 = publish.test()

        upstream.onNext(0, null, 1, null, 2)

        observer1.assertValues(0, null, 1, null, 2)
        observer2.assertValues(0, null, 1, null, 2)
    }

    @Test
    override fun does_not_multicast_to_old_observers_WHEN_disconnected() {
        val observer1 = publish.test()
        val observer2 = publish.test()
        publish.connectAndGetDisposable().dispose()

        upstream.onNext(0, null, 1, null, 2)

        observer1.assertNoValues()
        observer2.assertNoValues()
    }

    @Test
    override fun does_not_multicast_to_old_observers_WHEN_reconnected() {
        val observer1 = publish.test()
        val observer2 = publish.test()
        publish.connectAndGetDisposable().dispose()
        publish.connect()

        upstream.onNext(0, null, 1, null, 2)

        observer1.assertNoValues()
        observer2.assertNoValues()
    }

    @Test
    override fun multicasts_values_to_all_new_observers_WHEN_subscribed_after_reconnected() {
        publish.connectAndGetDisposable().dispose()
        publish.connect()
        val observer1 = publish.test()
        val observer2 = publish.test()

        upstream.onNext(0, null, 1, null, 2)

        observer1.assertValues(0, null, 1, null, 2)
        observer2.assertValues(0, null, 1, null, 2)
    }

    @Test
    override fun provides_disposable_synchronously_WHEN_connected_before_subscribed() {
        publish.connectAndGetDisposable()
    }

    @Test
    override fun provides_disposable_synchronously_WHEN_connected_after_subscribed() {
        publish.test()
        publish.connectAndGetDisposable()
    }

    @Test
    override fun provides_same_disposable_WHEN_connected_second_time() {
        val disposable1 = publish.connectAndGetDisposable()
        val disposable2 = publish.connectAndGetDisposable()

        assertSame(disposable1, disposable2)
    }

    @Test
    override fun does_not_subscribe_to_upstream_WHEN_disconnected_from_callback() {
        publish.connect(Disposable::dispose)

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    override fun provides_different_disposable_WHEN_reconnected() {
        val disposable1 = publish.connectAndGetDisposable()
        disposable1.dispose()
        val disposable2 = publish.connectAndGetDisposable()

        assertNotSame(disposable1, disposable2)
    }

    @Test
    override fun unsubscribes_from_upstream_WHEN_disconnected() {
        publish.connectAndGetDisposable().dispose()

        assertFalse(upstream.hasSubscribers)
    }

    override fun does_not_complete_any_observer_WHEN_disconnected() {
        val observer1 = publish.test()
        val observer2 = publish.test()

        publish.connectAndGetDisposable().dispose()

        observer1.assertNotComplete()
        observer2.assertNotComplete()
    }

    @Test
    override fun does_not_complete_new_observers_WHEN_reconnected_and_disconnected_from_old_again() {
        val disposable = publish.connectAndGetDisposable()
        disposable.dispose()
        publish.connect()
        val observer1 = publish.test()
        val observer2 = publish.test()

        disposable.dispose()

        observer1.assertSubscribed()
        observer2.assertSubscribed()
    }

    @Test
    override fun multicasts_values_to_new_observers_WHEN_reconnected_and_disconnected_from_old_again() {
        val disposable = publish.connectAndGetDisposable()
        disposable.dispose()
        publish.connect()
        val observer1 = publish.test()
        val observer2 = publish.test()
        disposable.dispose()

        upstream.onNext(0, null, 1, null, 2)

        observer1.assertValues(0, null, 1, null, 2)
        observer2.assertValues(0, null, 1, null, 2)
    }

    @Test
    override fun completes_all_observers_WHEN_connected_and_upstream_is_completed() {
        val observer1 = publish.test()
        val observer2 = publish.test()
        publish.connect()

        upstream.onComplete()

        observer1.assertComplete()
        observer2.assertComplete()
    }

    @Test
    override fun multicasts_error_to_all_observers_WHEN_connected_and_upstream_produced_error() {
        val error = Exception()
        val observer1 = publish.test()
        val observer2 = publish.test()
        publish.connect()

        upstream.onError(error)

        observer1.assertError(error)
        observer2.assertError(error)
    }

    private fun ConnectableObservable<*>.connectAndGetDisposable(): Disposable {
        lateinit var disposable: Disposable
        connect { disposable = it }

        return disposable
    }
}
