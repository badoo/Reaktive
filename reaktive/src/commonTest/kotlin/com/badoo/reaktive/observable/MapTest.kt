package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class MapTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ map {} }) {

    private val upstream = TestObservable<String?>()
    private val observer = upstream.map { it?.length }.test()

    @Test
    fun maps_non_null_value() {
        upstream.onNext("abc")

        observer.assertValue(3)
    }

    @Test
    fun maps_null_value() {
        upstream.onNext(null)

        observer.assertValue(null)
    }

    @Test
    fun maps_values_WHEN_stream_of_values_is_emitted() {
        upstream.onNext(null, "abc", "a")

        observer.assertValues(null, 3, 1)
    }

    @Test
    fun produces_error_WHEN_mapper_throws_an_exception() {
        val error = Throwable()

        val observer = upstream.map { throw error }.test()
        upstream.onNext("abc")

        observer.assertError(error)
    }
}
