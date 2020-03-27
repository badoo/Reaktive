package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
import kotlin.test.Ignore
import kotlin.test.Test

class OnErrorCompleteTest : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ onErrorComplete() }) {

    private val upstream = TestCompletable()
    private val observer = upstream.onErrorComplete().test()

    @Ignore
    @Test
    override fun produces_error_WHEN_upstream_produced_error() {
        // not applicable
    }

    @Test
    fun completes_WHEN_upstream_produced_error() {
        upstream.onError(Exception())

        observer.assertComplete()
    }
}
