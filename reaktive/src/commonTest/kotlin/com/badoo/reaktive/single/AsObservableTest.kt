package com.badoo.reaktive.single

import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test

class AsObservableTest : SingleToObservableTests by SingleToObservableTestsImpl({ asObservable() }) {

    private val upstream = TestSingle<Int?>()
    private val observer = upstream.asObservable().test()

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
