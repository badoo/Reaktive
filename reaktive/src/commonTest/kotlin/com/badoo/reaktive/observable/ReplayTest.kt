package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class ReplayTest : PublishGenericTests by PublishGenericTestsImpl({ replay() }) {

    private val upstream = TestObservable<Int?>()

    @Test
    fun emits_last_bufferSize_values_since_connection_in_the_same_order_to_the_first_subscriber() {
        val replay = upstream.replay(bufferSize = 3)
        replay.connect()
        upstream.onNext(-1)
        upstream.onNext(0)
        upstream.onNext(null)
        upstream.onNext(1)

        val observer = replay.test()

        observer.assertValues(0, null, 1)
    }

    @Test
    fun emits_last_bufferSize_values_since_connection_in_the_same_order_to_the_second_subscriber() {
        val replay = upstream.replay(bufferSize = 5)
        replay.connect()
        upstream.onNext(-1)
        upstream.onNext(0)
        upstream.onNext(null)
        upstream.onNext(1)
        replay.test()
        upstream.onNext(null)
        upstream.onNext(2)

        val observer = replay.test()

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun does_not_emit_any_values_from_previous_connection_WHEN_reconnect_and_new_subscriber() {
        val replay = upstream.replay()
        lateinit var disposable: Disposable
        replay.connect { disposable = it }
        upstream.onNext(0)
        upstream.onNext(null)
        disposable.dispose()

        replay.connect()
        val observer = replay.test()

        observer.assertNoValues()
    }
}
