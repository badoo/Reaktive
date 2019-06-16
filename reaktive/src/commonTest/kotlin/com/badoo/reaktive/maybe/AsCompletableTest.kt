package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.completable.isComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.maybe.TestMaybe
import kotlin.test.Test
import kotlin.test.assertTrue

class AsCompletableTest : MaybeToCompletableTests by MaybeToCompletableTests({ asCompletable() }) {

    private val upstream = TestMaybe<Int?>()
    private val observer = upstream.asCompletable().test()

    @Test
    fun completes_WHEN_upstream_completed() {
        upstream.onComplete()

        assertTrue(observer.isComplete)
    }

    @Test
    fun completes_WHEN_upstream_succeeded_with_non_null_value() {
        upstream.onSuccess(0)

        assertTrue(observer.isComplete)
    }

    @Test
    fun completes_WHEN_upstream_succeeded_with_null_value() {
        upstream.onSuccess(null)

        assertTrue(observer.isComplete)
    }
}