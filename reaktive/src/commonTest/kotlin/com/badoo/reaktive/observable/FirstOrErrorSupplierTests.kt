package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.single.TestSingleObserver
import com.badoo.reaktive.test.single.isError
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.single.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FirstOrErrorSupplierTests : ObservableToSingleTests by ObservableToSingleTests<Unit>({ firstOrError(::Throwable) }) {

    private val upstream = TestObservable<Int>()
    private val error = Exception()
    private val observer = firstOrErrorSubscribe(error)

    // To avoid freezing of "this"
    private fun firstOrErrorSubscribe(error: Exception): TestSingleObserver<Int> =
        upstream.firstOrError { error }.test()

    @Test
    fun succeeds_with_upstream_value_WHEN_upstream_emitted_value() {
        upstream.onNext(0)

        assertEquals(0, observer.value)
    }

    @Test
    fun produces_error_WHEN_upstream_completed() {
        upstream.onComplete()

        assertTrue(observer.isError(error))
    }

    @Test
    fun disposes_upstream_WHEN_upstream_emitted_value() {
        upstream.onNext(0)

        assertTrue(upstream.isDisposed)
    }

    @Test
    fun does_not_dispose_upstream_WHEN_upstream_did_not_emit_values() {
        assertFalse(upstream.isDisposed)
    }

    @Test
    fun produces_error_WHEN_supplier_throws_exception() {
        val exception = Exception()

        val observer =
            upstream
                .firstOrError { throw exception }
                .test()

        upstream.onComplete()

        assertTrue(observer.isError(exception))
    }
}