package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.single.isError
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface MaybeToSingleTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun disposes_upstream_WHEN_disposed()

    companion object {
        operator fun <T> invoke(transform: Maybe<T>.() -> Single<*>): MaybeToSingleTests =
            object : MaybeToSingleTests {
                private val upstream = TestMaybe<T>()
                private val observer = upstream.transform().test()

                override fun calls_onSubscribe_only_once_WHEN_subscribed() {
                    assertEquals(1, observer.disposables.size)
                }

                override fun produces_error_WHEN_upstream_produced_error() {
                    upstream.onError(Throwable())

                    assertTrue(observer.isError)
                }

                override fun disposes_upstream_WHEN_disposed() {
                    observer.dispose()

                    assertTrue(upstream.isDisposed)
                }
            }
    }
}