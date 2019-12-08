package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TakeObservableTests :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ take(1) }) {

    private val upstream = TestObservable<Int?>()
    private val observer = upstream.take(10).test()

    @Test
    fun completes_WHEN_limit_of_0_is_reached() {
        val observer = upstream.take(0).test()

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_limit_of_1_is_reached() {
        val observer = upstream.take(1).test()

        upstream.onNext(42)

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_limit_of_2_is_reached() {
        val observer = upstream.take(2).test()

        upstream.onNext(42)
        upstream.onNext(84)

        observer.assertComplete()
    }

    @Test
    fun does_not_complete_until_take_limit_is_reached() {
        val observer = upstream.take(2).test()

        upstream.onNext(42)
        observer.assertNotComplete()

        upstream.onNext(84)
        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun produces_same_error_WHEN_upstream_produced_error() {
        val error = Exception()

        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun produces_values_in_correct_order_from_upstream() {
        upstream.onNext(42, null, 84, null)

        observer.assertValues(42, null, 84, null)
    }

    @Test
    fun counts_recursive_invocations() {
        val observer = upstream
            .take(1)
            .let { source ->
                observableUnsafe<Int?> { observer ->
                    source.subscribe(
                        object : ObservableObserver<Int?> by observer {
                            override fun onNext(value: Int?) {
                                upstream.onNext(48)
                                observer.onNext(value)
                            }
                        }
                    )
                }
            }
            .test()

        upstream.onNext(42)

        observer.assertValue(42)
    }

    @Test
    fun throws_error_when_take_is_called_with_a_limit_less_than_0() {
        assertFailsWith<IllegalArgumentException> {
            upstream.take(-2)
        }
    }
}
