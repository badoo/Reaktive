package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.isError
import com.badoo.reaktive.test.completable.test
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
        operator fun invoke(transform: Completable.() -> Completable): UpstreamDownstreamGenericTests =
            object : UpstreamDownstreamGenericTests {
                private val upstream = TestCompletable()
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