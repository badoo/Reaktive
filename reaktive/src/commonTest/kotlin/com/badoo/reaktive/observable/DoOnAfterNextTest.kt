package com.badoo.reaktive.observable

import com.badoo.reaktive.test.TestObservableRelay
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnAfterNextTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ doOnAfterNext {} }),
    ObservableToObservableForwardTests by ObservableToObservableForwardTestsImpl({ doOnAfterNext {} }) {

    private val upstream = TestObservable<Int>()

    @Test
    fun calls_action_after_emitting() {
        val callOrder = ArrayList<String>()

        upstream
            .doOnAfterNext { value ->
                callOrder += "action $value"
            }
            .subscribe(
                object : DefaultObservableObserver<Int> {
                    override fun onNext(value: Int) {
                        callOrder += "onNext $value"
                    }
                }
            )

        upstream.onNext(0)
        upstream.onNext(1)

        assertEquals(listOf("onNext 0", "action 0", "onNext 1", "action 1"), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_completed() {
        var isCalled = false

        upstream
            .doOnAfterNext { isCalled = true }
            .test()

        upstream.onComplete()

        assertFalse(isCalled)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_produced_error() {
        var isCalled = false

        upstream
            .doOnAfterNext { isCalled = true }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled)
    }

    @Test
    fun produces_error_WHEN_upstream_emitted_value_and_exception_in_lambda() {
        val error = Exception()

        val observer =
            upstream
                .doOnAfterNext { throw error }
                .test()

        upstream.onNext(0)

        observer.assertError(error)
    }

    @Test
    fun does_no_call_action_WHEN_previous_lambda_thrown_exception() {
        var count = 0

        val upstream = TestObservableRelay<Int>()
        upstream
            .doOnAfterNext {
                count++
                throw Exception()
            }
            .test()

        upstream.onNext(0)
        upstream.onNext(1)

        assertEquals(1, count)
    }
}
