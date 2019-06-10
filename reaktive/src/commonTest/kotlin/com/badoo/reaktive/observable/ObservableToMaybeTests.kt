package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.test.maybe.isComplete
import com.badoo.reaktive.test.maybe.isError
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface ObservableToMaybeTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun completes_WHEN_upstream_is_completed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun disposes_upstream_WHEN_disposed()

    companion object {
        operator fun <T> invoke(transform: Observable<T>.() -> Maybe<*>): ObservableToMaybeTests =
            object : ObservableToMaybeTests {
                private val upstream = TestObservable<T>()
                private val observer = upstream.transform().test()

                override fun calls_onSubscribe_only_once_WHEN_subscribed() {
                    assertEquals(1, observer.disposables.size)
                }

                override fun completes_WHEN_upstream_is_completed() {
                    upstream.onComplete()

                    assertTrue(observer.isComplete)
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