package com.badoo.reaktive.single

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test
import kotlin.test.assertFalse

interface SingleToMaybeTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun unsubscribes_from_upstream_WHEN_disposed()

    companion object {
        operator fun <T> invoke(transform: Single<T>.() -> Maybe<*>): SingleToMaybeTests =
            object : SingleToMaybeTests {
                private val upstream = TestSingle<T>()
                private val observer = upstream.transform().test()

                override fun calls_onSubscribe_only_once_WHEN_subscribed() {
                    observer.assertSubscribed()
                }

                override fun produces_error_WHEN_upstream_produced_error() {
                    val error = Throwable()

                    upstream.onError(error)

                    observer.assertError(error)
                }

                override fun unsubscribes_from_upstream_WHEN_disposed() {
                    observer.dispose()

                    assertFalse(upstream.hasSubscribers)
                }
            }
    }
}