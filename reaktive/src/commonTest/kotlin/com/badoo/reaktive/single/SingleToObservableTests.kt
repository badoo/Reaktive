package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test
import kotlin.test.assertFalse

interface SingleToObservableTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun unsubscribes_from_upstream_WHEN_disposed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_succeeded()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_produced_error()

    companion object {
        operator fun invoke(transform: Single<Unit>.() -> Observable<*>): SingleToObservableTests =
            object : SingleToObservableTests {
                private val upstream = TestSingle<Unit>()
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

                override fun disposes_downstream_disposable_WHEN_upstream_succeeded() {
                    upstream.onSuccess(Unit)

                    observer.assertDisposed()
                }

                override fun disposes_downstream_disposable_WHEN_upstream_produced_error() {
                    upstream.onError(Throwable())

                    observer.assertDisposed()
                }
            }
    }
}
