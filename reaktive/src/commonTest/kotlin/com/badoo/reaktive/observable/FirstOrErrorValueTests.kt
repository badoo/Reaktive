package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FirstOrErrorValueTests : ObservableToSingleTests by ObservableToSingleTests<Unit>({ firstOrError(Throwable()) }) {

    private val upstream = TestObservable<Int>()
    private val error = Exception()
    private val observer = upstream.firstOrError(error).test()

    @Test
    fun succeeds_with_upstream_value_WHEN_upstream_emitted_value() {
        upstream.onNext(0)

        observer.assertSuccess(0)
    }

    @Test
    fun produces_error_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertError(error)
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
}