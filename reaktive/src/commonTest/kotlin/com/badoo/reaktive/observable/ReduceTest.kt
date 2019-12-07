package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.onNext
import kotlin.test.Test

class ReduceTest : ObservableToMaybeTests by ObservableToMaybeTestsImpl({ reduce { _, _ -> Unit } }) {

    private val upstream = TestObservable<Int?>()

    private val observer =
        upstream
            .reduce { a, b -> if ((a != null) && (b != null)) a + b else null }
            .test()

    @Test
    fun completes_WHEN_upstream_produced_no_values_and_completed() {
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun succeeds_with_null_value_WHEN_upstream_produced_single_null_value_and_completed() {
        upstream.onNext(null)
        upstream.onComplete()

        observer.assertSuccess(null)
    }

    @Test
    fun succeeds_with_non_null_value_WHEN_upstream_produced_single_non_null_value_and_completed() {
        upstream.onNext(0)
        upstream.onComplete()

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_with_reduced_null_value_WHEN_upstream_produced_multiple_values_and_completed() {
        upstream.onNext(0, null, 1)
        upstream.onComplete()

        observer.assertSuccess(null)
    }

    @Test
    fun succeeds_with_reduced_non_null_value_WHEN_upstream_produced_multiple_values_and_completed() {
        upstream.onNext(0, 1, 2)
        upstream.onComplete()

        observer.assertSuccess(3)
    }

    @Test
    fun produces_error_WHEN_reducer_thrown_exception() {
        val error = Exception()

        val observer =
            upstream
                .reduce { _, _ -> throw error }
                .test()

        upstream.onNext(0, 1)

        observer.assertError(error)
    }
}
