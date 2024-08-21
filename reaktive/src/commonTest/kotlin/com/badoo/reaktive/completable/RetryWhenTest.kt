package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.observableOf
import com.badoo.reaktive.observable.retryWhen
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RetryWhenTest : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ retryWhen { it } }) {

    private val upstream = TestCompletable()
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
    fun disposes_retry_observable_WHEN_upstream_completed() {
        upstream.retryWhen { retryObservable }.test()

        upstream.onComplete()

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
    fun does_not_complete_WHEN_upstream_produced_error() {
        val observer = upstream.retryWhen { retryObservable }.test()

        upstream.onError(Exception())

        assertFalse(observer.isComplete)
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
    fun completes_WHEN_retry_observable_completed() {
        val observer = upstream.retryWhen { retryObservable }.test()

        retryObservable.onComplete()

        observer.assertComplete()
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
    fun completes_WHEN_resubscribed_after_error_and_upstream_completed() {
        val observer = upstream.retryWhen { retryObservable }.test()
        upstream.onError(Exception())
        upstream.reset()
        retryObservable.onNext(1)

        upstream.onComplete()

        observer.assertComplete()
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
