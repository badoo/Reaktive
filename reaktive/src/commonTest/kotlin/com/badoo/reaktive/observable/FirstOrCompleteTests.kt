package com.badoo.reaktive.observable

import com.badoo.reaktive.test.maybe.isComplete
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.maybe.value
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FirstOrCompleteTests : ObservableToMaybeTests by ObservableToMaybeTests<Nothing>({ firstOrComplete() }) {

    private val upstream = TestObservable<Int>()
    private val observer = upstream.firstOrComplete().test()

    @Test
    fun succeeds_WHEN_upstream_emitted_value() {
        upstream.onNext(0)

        assertEquals(0, observer.value)
    }

    @Test
    fun completes_WHEN_upstream_completed() {
        upstream.onComplete()

        assertTrue(observer.isComplete)
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