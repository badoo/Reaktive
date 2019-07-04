package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertTrue

interface CompletableToObservableTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun completes_WHEN_upstream_completed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun disposes_upstream_WHEN_disposed()

    companion object {
        operator fun invoke(transform: Completable.() -> Observable<*>): CompletableToObservableTests =
            object : CompletableToObservableTests {
                private val upstream = TestCompletable()
                private val observer = upstream.transform().test()

                override fun calls_onSubscribe_only_once_WHEN_subscribed() {
                    observer.assertSubscribed()
                }

                override fun completes_WHEN_upstream_completed() {
                    upstream.onComplete()

                    observer.assertComplete()
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