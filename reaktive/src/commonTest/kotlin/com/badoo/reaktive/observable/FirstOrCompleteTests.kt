package com.badoo.reaktive.observable

import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FirstOrCompleteTests : ObservableToMaybeTests by ObservableToMaybeTests<Nothing>({ firstOrComplete() }) {

    private val upstream = TestObservable<Int>()
    private val observer = upstream.firstOrComplete().test()

    @Test
    fun succeeds_WHEN_upstream_emitted_value() {
        upstream.onNext(0)

        observer.assertSuccess(0)
    }

    @Test
    fun completes_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertComplete()
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