package com.badoo.reaktive.base

import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.test.base.TestSource
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.base.hasSubscribers
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse

interface SourceTests {

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed()

    @Test
    fun produces_error_WHEN_upstream_produced_error()

    @Test
    fun unsubscribes_from_upstream_WHEN_disposed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_produced_error()
}

@Ignore
class SourceTestsImpl<S : TestSource<*>>(
    private val upstream: S,
    private val subscribeTest: S.() -> TestObserver
) : SourceTests {

    private val observer = upstream.subscribeTest()

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

    override fun disposes_downstream_disposable_WHEN_upstream_produced_error() {
        upstream.onError(Throwable())

        observer.assertDisposed()
    }
}
