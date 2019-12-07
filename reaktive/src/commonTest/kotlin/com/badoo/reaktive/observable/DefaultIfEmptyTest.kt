package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class DefaultIfEmptyTest
    : ObservableToObservableTests by ObservableToObservableTestsImpl({ defaultIfEmpty(10) }) {

    @Test
    fun should_return_default_value_when_source_is_empty() {
        val source = TestObservable<Int>()
        val observer = source.defaultIfEmpty(42).test()

        source.onComplete()

        observer.assertValue(42)
    }

    @Test
    fun should_not_return_default_value_when_source_is_not_empty() {
        val source = TestObservable<Int>()
        val observer = source.defaultIfEmpty(42).test()

        source.onNext(1)
        source.onComplete()

        observer.assertValue(1)
    }

    @Test
    fun should_not_return_default_value_when_source_emits_null() {
        val source = TestObservable<Int?>()
        val observer = source.defaultIfEmpty(42).test()

        source.onNext(null)
        source.onComplete()

        observer.assertValue(null)
    }
}
