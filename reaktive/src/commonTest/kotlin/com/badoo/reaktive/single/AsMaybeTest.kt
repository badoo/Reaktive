package com.badoo.reaktive.single

import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test

class AsMaybeTest : SingleToMaybeTests by SingleToMaybeTestsImpl({ asMaybe() }) {

    private val upstream = TestSingle<Int?>()
    private val observer = upstream.asMaybe().test()

    @Test
    fun succeeds_WHEN_upstream_succeeded_with_non_null_value() {
        upstream.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_WHEN_upstream_succeeded_with_null_value() {
        upstream.onSuccess(null)

        observer.assertSuccess(null)
    }
}
