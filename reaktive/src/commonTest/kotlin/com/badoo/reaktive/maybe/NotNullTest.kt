package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Test

class NotNullTest : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ notNull() }) {

    private val upstream = TestMaybe<Int?>()
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
