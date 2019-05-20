package com.badoo.reaktive.observable

import com.badoo.reaktive.testutils.TestObservable
import com.badoo.reaktive.testutils.dispose
import com.badoo.reaktive.testutils.isCompleted
import com.badoo.reaktive.testutils.isError
import com.badoo.reaktive.testutils.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface UpstreamDownstreamGenericTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun completes_WHEN_upstream_is_completed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun disposes_upstream_WHEN_disposed()

    companion object {
        operator fun <T> invoke(transform: Observable<T>.() -> Observable<*>): UpstreamDownstreamGenericTests =
            object : UpstreamDownstreamGenericTests {
                private val upstream = TestObservable<T>()
                private val observer = upstream.transform().test()

                override fun calls_onSubscribe_only_once_WHEN_subscribed() {
                    assertEquals(1, observer.disposables.size)
                }

                override fun completes_WHEN_upstream_is_completed() {
                    upstream.onComplete()

                    assertTrue(observer.isCompleted)
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