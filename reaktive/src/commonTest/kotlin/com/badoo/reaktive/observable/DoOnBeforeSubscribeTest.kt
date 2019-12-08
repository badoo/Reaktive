package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeSubscribeTest
    : ObservableToObservableTests by ObservableToObservableTestsImpl({ doOnBeforeSubscribe {} }) {

    @Test
    fun calls_action_before_downstream_onSubscribe_WHEN_action_does_not_throw_exception() {
        val callOrder = SharedList<String>()

        observableUnsafe<Nothing> {}
            .doOnBeforeSubscribe {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultObservableObserver<Nothing> {
                    override fun onSubscribe(disposable: Disposable) {
                        callOrder += "onSubscribe"
                    }
                }
            )

        assertEquals(listOf("action", "onSubscribe"), callOrder)
    }

    @Test
    fun delegates_error_to_downstream_after_downstream_onSubscribe_WHEN_action_throws_exception() {
        val callOrder = SharedList<Any>()
        val exception = Exception()

        observableUnsafe<Nothing> {}
            .doOnBeforeSubscribe {
                throw exception
            }
            .subscribe(
                object : DefaultObservableObserver<Nothing> {
                    override fun onSubscribe(disposable: Disposable) {
                        callOrder += "onSubscribe"
                    }

                    override fun onError(error: Throwable) {
                        callOrder += error
                    }
                }
            )

        assertEquals(listOf<Any>("onSubscribe", exception), callOrder)
    }

    @Test
    fun disposes_downstream_disposable_WHEN_action_throws_exception() {
        val observer =
            observableUnsafe<Nothing> {}
                .doOnBeforeSubscribe { throw Exception() }
                .test()

        observer.assertDisposed()
    }

    @Test
    fun does_not_call_action_WHEN_onSubscribe_received_from_upstream() {
        val isCalled = AtomicBoolean()

        observableUnsafe<Nothing> { observer ->
            isCalled.value = false
            observer.onSubscribe(Disposable())
        }
            .doOnBeforeSubscribe {
                isCalled.value = true
            }
            .test()

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_emitted_value() {
        val isCalled = AtomicBoolean()
        val upstream = TestObservable<Int>()

        upstream
            .doOnBeforeSubscribe {
                isCalled.value = true
            }
            .test()

        isCalled.value = false
        upstream.onNext(0)

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_completed() {
        val isCalled = AtomicBoolean()
        val upstream = TestObservable<Nothing>()

        upstream
            .doOnBeforeSubscribe {
                isCalled.value = true
            }
            .test()

        isCalled.value = false
        upstream.onComplete()

        assertFalse(isCalled.value)
    }


    @Test
    fun does_not_call_action_WHEN_produced_error() {
        val isCalled = AtomicBoolean()
        val upstream = TestObservable<Nothing>()

        upstream
            .doOnBeforeSubscribe {
                isCalled.value = true
            }
            .test()

        isCalled.value = false
        upstream.onError(Throwable())

        assertFalse(isCalled.value)
    }
}
