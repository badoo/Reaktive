package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.maybe.TestMaybe
import kotlin.test.Test

class AsCompletableTest : MaybeToCompletableTests by MaybeToCompletableTestsImpl({ asCompletable() }) {

    private val upstream = TestMaybe<Int?>()
    private val observer = upstream.asCompletable().test()

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
