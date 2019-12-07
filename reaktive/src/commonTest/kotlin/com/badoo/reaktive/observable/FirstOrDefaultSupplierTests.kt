package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FirstOrDefaultSupplierTests : ObservableToSingleTests by ObservableToSingleTestsImpl({ firstOrDefault { Unit } }) {

    private val upstream = TestObservable<Int>()
    private val observer = upstream.firstOrDefault { -1 }.test()

    @Test
    fun succeeds_with_upstream_value_WHEN_upstream_emitted_value() {
        upstream.onNext(0)

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_with_default_value_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertSuccess(-1)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_upstream_emitted_value() {
        upstream.onNext(0)

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_upstream_WHEN_upstream_did_not_emit_values() {
        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun produces_error_WHEN_supplier_throws_exception() {
        val exception = Exception()

        val observer =
            upstream
                .firstOrDefault { throw exception }
                .test()

        upstream.onComplete()

        observer.assertError(exception)
    }
}
