package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test

class AsSingleValueTests : CompletableToSingleTests by CompletableToSingleTestsImpl({ asSingle(0) }) {

    @Test
    fun succeeds_with_default_value_WHEN_upstream_completed() {
        val upstream = TestCompletable()
        val observer = upstream.asSingle(0).test()

        upstream.onComplete()

        observer.assertSuccess(0)
    }
}
