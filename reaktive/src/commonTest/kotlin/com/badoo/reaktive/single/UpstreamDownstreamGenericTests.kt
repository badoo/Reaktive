package com.badoo.reaktive.single

import com.badoo.reaktive.test.maybe.isError
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.isError
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface UpstreamDownstreamGenericTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun disposes_upstream_WHEN_disposed()

    companion object {
        operator fun <T> invoke(transform: Single<T>.() -> Single<*>): UpstreamDownstreamGenericTests =
            object : UpstreamDownstreamGenericTests {
                private val upstream = TestSingle<T>()
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

    object TransformToMaybe {
        operator fun <T> invoke(transform: Single<T>.() -> com.badoo.reaktive.maybe.Maybe<*>): UpstreamDownstreamGenericTests =
            object : UpstreamDownstreamGenericTests {
                private val upstream = TestSingle<T>()
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