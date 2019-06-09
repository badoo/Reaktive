package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.isComplete
import com.badoo.reaktive.test.observable.isError
import com.badoo.reaktive.test.observable.values
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscribeTest {

    private val upstream = TestObservable<Int?>()

    @Test
    fun returned_disposable_is_not_disposed() {
        assertFalse(upstream.subscribe().isDisposed)
    }

    @Test
    fun disposes_upstream_WHEN_disposed() {
        upstream.subscribe().dispose()

        assertTrue(upstream.isDisposed)
    }

    @Test
    fun calls_onSubscribe() {
        val observer = TestObservableObserver<Unit>()

        upstream.subscribe(onSubscribe = observer::onSubscribe)

        assertEquals(1, observer.disposables.size)
    }

    @Test
    fun calls_onNext_in_the_same_order_WHEN_upstream_emitted_values() {
        val observer = TestObservableObserver<Int?>()
        upstream.subscribe(onNext = observer::onNext)

        upstream.onNext(null)
        upstream.onNext(1)
        upstream.onNext(2)

        assertEquals(listOf(null, 1, 2), observer.values)
    }

    @Test
    fun calls_onComplete_WHEN_upstream_is_completed() {
        val observer = TestObservableObserver<Int>()
        upstream.subscribe(onComplete = observer::onComplete)

        upstream.onComplete()

        assertTrue(observer.isComplete)
    }

    @Test
    fun disposes_disposable_WHEN_upstream_is_completed() {
        val disposable = upstream.subscribe()

        upstream.onComplete()

        assertTrue(disposable.isDisposed)
    }


    @Test
    fun calls_onError_WHEN_upstream_produced_an_error() {
        val observer = TestObservableObserver<Int>()
        upstream.subscribe(onError = observer::onError)
        val error = Throwable()

        upstream.onError(error)

        assertTrue(observer.isError(error))
    }

    @Test
    fun disposes_disposable_WHEN_upstream_produced_an_error() {
        val disposable = upstream.subscribe(onError = {})

        upstream.onError(Throwable())

        assertTrue(disposable.isDisposed)
    }
}