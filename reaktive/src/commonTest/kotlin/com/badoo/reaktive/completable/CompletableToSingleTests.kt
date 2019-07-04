package com.badoo.reaktive.completable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertTrue

interface CompletableToSingleTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun disposes_upstream_WHEN_disposed()

    companion object {
        operator fun invoke(transform: Completable.() -> Single<*>): CompletableToSingleTests =
            object : CompletableToSingleTests {
                private val upstream = TestCompletable()
                private val observer = upstream.transform().test()

                override fun calls_onSubscribe_only_once_WHEN_subscribed() {
                    observer.assertSubscribed()
                }

                override fun produces_error_WHEN_upstream_produced_error() {
                    val error = Throwable()

                    upstream.onError(error)

                    observer.assertError(error)
                }

                override fun disposes_upstream_WHEN_disposed() {
                    observer.dispose()

                    assertTrue(upstream.isDisposed)
                }
            }
    }
}