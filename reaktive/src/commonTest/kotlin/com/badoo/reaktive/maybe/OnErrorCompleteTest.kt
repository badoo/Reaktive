package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Ignore
import kotlin.test.Test

class OnErrorCompleteTest : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ onErrorComplete() }) {

    private val upstream = TestMaybe<Int?>()
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
