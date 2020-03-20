package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.singleOf
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertFalse

class SwitchIfEmptySingleTest : MaybeToSingleTests by MaybeToSingleTestsImpl({ switchIfEmpty(singleOf(0)) }) {

    private val upstream = TestMaybe<Int?>()
    private val other = TestSingle<Int?>()
    private val observer = upstream.switchIfEmpty(other).test()

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

    @Test
    fun does_not_subscribe_to_other_IF_upstream_not_finished() {
        assertFalse(other.hasSubscribers)
    }

    @Test
    fun does_not_subscribe_to_other_WHEN_upstream_succeeded() {
        upstream.onSuccess(0)

        assertFalse(other.hasSubscribers)
    }

    @Test
    fun subscribes_to_other_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertSubscribed()
    }

    @Test
    fun succeeds_WHEN_upstream_completed_and_other_succeeded_with_non_null_value() {
        upstream.onComplete()
        other.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_WHEN_upstream_completed_and_other_succeeded_with_null_value() {
        upstream.onComplete()
        other.onSuccess(null)

        observer.assertSuccess(null)
    }

    @Test
    fun produces_error_WHEN_upstream_completed_and_other_produced_error() {
        val error = Exception()
        upstream.onComplete()
        other.onError(error)

        observer.assertError(error)
    }
}
