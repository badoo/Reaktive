package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class ReplayTest : PublishGenericTests by PublishGenericTestsImpl({ replay() }) {

    private val upstream = TestObservable<Int?>()

    @Test
    fun emits_last_bufferSize_values_since_connection_in_the_same_order_to_the_first_subscriber() {
        val replay = upstream.replay(bufferSize = 3)
        replay.connect()
        upstream.onNext(-1, 0, null, 1)

        val observer = replay.test()

        observer.assertValues(0, null, 1)
    }

    @Test
    fun emits_last_bufferSize_values_since_connection_in_the_same_order_to_the_first_subscriber_after_disconnect() {
        val replay = upstream.replay(bufferSize = 3)
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(-1, 0, null, 1)
        disposable.dispose()

        val observer = replay.test()

        observer.assertValues(0, null, 1)
    }

    @Test
    fun emits_last_bufferSize_values_since_connection_in_the_same_order_to_the_second_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        replay.connect()
        upstream.onNext(-1, 0, null, 1)
        replay.test()
        upstream.onNext(null, 2)

        val observer = replay.test()

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun emits_last_bufferSize_values_since_connection_in_the_same_order_to_the_second_subscriber_after_disconnect() {
        val replay = upstream.replay(bufferSize = 5)
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(-1, 0, null, 1)
        replay.test()
        upstream.onNext(null, 2)
        disposable.dispose()

        val observer = replay.test()

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun does_not_emit_any_values_from_previous_connection_WHEN_reconnect_and_new_subscriber() {
        val replay = upstream.replay()
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(0, null)
        disposable.dispose()

        replay.connect()
        val observer = replay.test()

        observer.assertNoValues()
    }

    @Test
    fun does_not_emit_new_values_after_disconnect() {
        val replay = upstream.replay()
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        val observer = replay.test()
        disposable.dispose()
        upstream.onNext(0)

        observer.assertNoValues()
    }

    @Test
    fun emits_last_bufferSize_values_in_the_same_order_WHEN_upstream_completed_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        replay.connect()
        upstream.onNext(-1, 0, null, 1, null, 2)
        upstream.onComplete()

        val observer = replay.test()

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun emits_last_bufferSize_values_in_the_same_order_WHEN_disconnected_and_upstream_completed_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(-1, 0, null, 1, null, 2)
        disposable.dispose()
        upstream.onComplete()

        val observer = replay.test()

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun emits_last_bufferSize_values_in_the_same_order_WHEN_upstream_completed_and_disconnected_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(-1, 0, null, 1, null, 2)
        upstream.onComplete()
        disposable.dispose()

        val observer = replay.test()

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun completes_WHEN_upstream_completed_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        replay.connect()
        upstream.onNext(0)
        upstream.onComplete()

        val observer = replay.test()

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_upstream_completed_and_disconnected_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(0)
        upstream.onComplete()
        disposable.dispose()

        val observer = replay.test()

        observer.assertComplete()
    }

    @Test
    fun does_not_complete_WHEN_disconnected_and_upstream_completed_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(0)
        disposable.dispose()
        upstream.onComplete()

        val observer = replay.test()

        observer.assertNotComplete()
    }

    @Test
    fun emits_last_bufferSize_values_in_the_same_order_WHEN_upstream_produced_error_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        replay.connect()
        upstream.onNext(-1, 0, null, 1, null, 2)
        upstream.onError(Throwable())

        val observer = replay.test()

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun emits_last_bufferSize_values_in_the_same_order_WHEN_disconnected_and_upstream_produced_error_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(-1, 0, null, 1, null, 2)
        disposable.dispose()
        upstream.onError(Throwable())

        val observer = replay.test()

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun emits_last_bufferSize_values_in_the_same_order_WHEN_upstream_produced_error_and_disconnected_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(-1, 0, null, 1, null, 2)
        upstream.onError(Throwable())
        disposable.dispose()

        val observer = replay.test()

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        replay.connect()
        upstream.onNext(0)
        val error = Throwable()
        upstream.onError(error)

        val observer = replay.test()

        observer.assertError(error)
    }

    @Test
    fun produces_error_WHEN_upstream_produced_error_and_disconnected_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(0)
        val error = Throwable()
        upstream.onError(error)
        disposable.dispose()

        val observer = replay.test()

        observer.assertError(error)
    }

    @Test
    fun does_not_produce_error_WHEN_disconnected_and_upstream_produced_error_and_new_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(0)
        disposable.dispose()
        upstream.onError(Throwable())

        val observer = replay.test()

        observer.assertNotError()
    }
}
