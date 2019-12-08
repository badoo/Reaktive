package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class AsObservableTest : MaybeToObservableTests by MaybeToObservableTestsImpl({ asObservable() }) {

    private val upstream = TestMaybe<Int?>()
    private val observer = upstream.asObservable().test()

    @Test
    fun completes_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun emits_value_WHEN_upstream_succeeded_with_non_null_value() {
        upstream.onSuccess(0)

        observer.assertValue(0)
    }

    @Test
    fun emits_value_WHEN_upstream_succeeded_with_null_value() {
        upstream.onSuccess(null)

        observer.assertValue(null)
    }

    @Test
    fun completes_WHEN_upstream_succeeded_with_non_null_value() {
        upstream.onSuccess(0)

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_upstream_succeeded_with_null_value() {
        upstream.onSuccess(null)

        observer.assertComplete()
    }
}
