package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertFalse

interface MaybeToObservableTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun completes_WHEN_upstream_is_completed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun unsubscribes_from_upstream_WHEN_disposed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_completed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_succeeded()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_produced_error()

    companion object {
        operator fun invoke(transform: Maybe<Unit>.() -> Observable<*>): MaybeToObservableTests =
            object : MaybeToObservableTests {
                private val upstream = TestMaybe<Unit>()
                private val observer = upstream.transform().test()

                override fun calls_onSubscribe_only_once_WHEN_subscribed() {
                    observer.assertSubscribed()
                }

                override fun completes_WHEN_upstream_is_completed() {
                    upstream.onComplete()

                    observer.assertComplete()
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

                override fun disposes_downstream_disposable_WHEN_upstream_completed() {
                    upstream.onComplete()

                    observer.assertDisposed()
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
