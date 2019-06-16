package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.observable.isComplete
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.observable.values
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AsObservableTest : MaybeToObservableTests by MaybeToObservableTests<Nothing>({ asObservable() }) {

    private val upstream = TestMaybe<Int?>()
    private val observer = upstream.asObservable().test()

    @Test
    fun completes_WHEN_upstream_completed() {
        upstream.onComplete()

        assertTrue(observer.isComplete)
    }

    @Test
    fun emits_value_WHEN_upstream_succeeded_with_non_null_value() {
        upstream.onSuccess(0)

        assertEquals(listOf(0), observer.values)
    }

    @Test
    fun emits_value_WHEN_upstream_succeeded_with_null_value() {
        upstream.onSuccess(null)

        assertEquals(listOf(null), observer.values)
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