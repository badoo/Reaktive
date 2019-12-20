package com.badoo.reaktive.single

import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test

class NotNullTest : SingleToMaybeTests by SingleToMaybeTestsImpl({ notNull() }) {

    private val upstream = TestSingle<Int?>()
    private val observer = upstream.notNull().test()

    @Test
    fun passes_not_null_value() {
        upstream.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun filters_null_value() {
        upstream.onSuccess(null)

        observer.assertComplete()
    }
}
