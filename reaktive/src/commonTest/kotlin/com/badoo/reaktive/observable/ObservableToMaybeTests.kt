package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.test.Test
import kotlin.test.assertFalse

interface ObservableToMaybeTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun completes_WHEN_upstream_is_completed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun unsubscribes_from_upstream_WHEN_disposed()

    companion object {
        operator fun <T> invoke(transform: Observable<T>.() -> Maybe<*>): ObservableToMaybeTests =
            object : ObservableToMaybeTests {
                private val upstream = TestObservable<T>()
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
            }
    }
}