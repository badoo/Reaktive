package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscribeTest {

    private val upstream = TestObservable<Int?>()
    private val observer = TestObservableObserver<Int?>()

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
        upstream.subscribe(onSubscribe = observer::onSubscribe)

        observer.assertSubscribed()
    }

    @Test
    fun calls_onNext_in_the_same_order_WHEN_upstream_emitted_values() {
        observer.onSubscribe(disposable())
        upstream.subscribe(onNext = observer::onNext)

        upstream.onNext(null)
        upstream.onNext(1)
        upstream.onNext(2)

        observer.assertValues(null, 1, 2)
    }

    @Test
    fun calls_onComplete_WHEN_upstream_is_completed() {
        observer.onSubscribe(disposable())
        upstream.subscribe(onComplete = observer::onComplete)

        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun disposes_disposable_WHEN_upstream_is_completed() {
        val disposable = upstream.subscribe()

        upstream.onComplete()

        assertTrue(disposable.isDisposed)
    }


    @Test
    fun calls_onError_WHEN_upstream_produced_an_error() {
        observer.onSubscribe(disposable())
        upstream.subscribe(onError = observer::onError)
        val error = Throwable()

        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun disposes_disposable_WHEN_upstream_produced_an_error() {
        val disposable = upstream.subscribe(onError = {})

        upstream.onError(Throwable())

        assertTrue(disposable.isDisposed)
    }
}