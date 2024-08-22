package com.badoo.reaktive.single

import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.observableOf
import com.badoo.reaktive.observable.retryWhen
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RetryWhenTest : SingleToSingleTests by SingleToSingleTestsImpl({ retryWhen { it } }) {

    private val upstream = TestSingle<Int>()
    private val retryObservable = TestObservable<Any?>()

    @Ignore
    @Test
    override fun disposes_downstream_disposable_WHEN_upstream_produced_error() {
        // Not applicable
    }

    @Ignore
    @Test
    override fun produces_error_WHEN_upstream_produced_error() {
        // Not applicable
    }

    @Test
    fun calls_handler_WHEN_subscribed() {
        var isCalled = false

        upstream
            .retryWhen {
                isCalled = true
                retryObservable
            }
            .test()

        assertTrue(isCalled)
    }

    @Test
    fun produces_error_WHEN_handler_throws() {
        val exception = Exception()

        val observer = upstream.retryWhen { throw exception }.test()

        observer.assertError(exception)
    }

    @Test
    fun subscribes_to_retry_observable_WHEN_subscribed() {
        upstream.retryWhen { retryObservable }.test()

        assertEquals(1, retryObservable.subscriptionCount)
    }

    @Test
    fun disposes_retry_observable_WHEN_disposed() {
        val observer = upstream.retryWhen { retryObservable }.test()

        observer.dispose()

        assertFalse(retryObservable.hasSubscribers)
    }

    @Test
    fun disposes_retry_observable_WHEN_upstream_succeeded() {
        upstream.retryWhen { retryObservable }.test()

        upstream.onSuccess(1)

        assertFalse(retryObservable.hasSubscribers)
    }

    @Test
    fun does_not_dispose_retry_observable_WHEN_upstream_produced_error() {
        upstream.retryWhen { retryObservable }.test()

        upstream.onError(Exception())

        assertEquals(1, retryObservable.subscriptionCount)
    }

    @Test
    fun does_not_produce_error_WHEN_upstream_produced_error() {
        val observer = upstream.retryWhen { retryObservable }.test()

        upstream.onError(Exception())

        assertNull(observer.error)
    }

    @Test
    fun error_observable_emits_exception_WHEN_upstream_produced_error() {
        val exception = Exception()
        lateinit var errorObserver: TestObservableObserver<Any?>

        upstream
            .retryWhen {
                errorObserver = it.test()
                retryObservable
            }
            .test()

        upstream.onError(exception)

        errorObserver.assertValue(exception)
    }

    @Test
    fun produces_error_WHEN_retry_observable_completed() {
        val observer = upstream.retryWhen { retryObservable }.test()

        retryObservable.onComplete()

        observer.assertError { it is NoSuchElementException }
    }

    @Test
    fun produces_error_WHEN_retry_observable_produced_error() {
        val exception = Exception()
        val observer = upstream.retryWhen { retryObservable }.test()

        retryObservable.onError(exception)

        observer.assertError(exception)
    }

    @Test
    fun does_not_produce_error_WHEN_retry_observable_emitted_value() {
        val observer = upstream.retryWhen { retryObservable }.test()

        retryObservable.onNext(1)

        observer.assertNotError()
    }

    @Test
    fun subscribes_to_upstream_WHEN_upstream_produced_error_and_retry_observable_emitted_value() {
        upstream.retryWhen { retryObservable }.test()
        upstream.onError(Exception())
        upstream.reset()

        retryObservable.onNext(1)

        assertEquals(1, upstream.subscriptionCount)
    }

    @Test
    fun succeeds_WHEN_resubscribed_after_error_and_upstream_succeeded() {
        val observer = upstream.retryWhen { retryObservable }.test()
        upstream.onError(Exception())
        upstream.reset()
        retryObservable.onNext(1)

        upstream.onSuccess(1)

        observer.assertSuccess(1)
    }

    @Test
    fun resubscribes_to_upstream_only_once_WHEN_upstream_produced_error_and_retry_observable_emitted_multiple_values_synchronously() {
        upstream
            .retryWhen { errors ->
                errors.flatMap {
                    upstream.reset()
                    observableOf(1, 2)
                }
            }.test()

        upstream.onError(Exception())

        assertEquals(1, upstream.subscriptionCount)
    }
}
