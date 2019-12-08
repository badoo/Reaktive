package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class SkipTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ skip(0) }) {

    @Test
    fun should_skip_n_values() {
        val source = TestObservable<Int>()
        val observer = source
            .skip(2)
            .test()

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        source.onNext(4)

        observer.assertValues(3, 4)
    }

    @Test
    fun should_skip_null() {
        val source = TestObservable<Int?>()
        val observer = source
            .skip(2)
            .test()

        source.onNext(null)
        source.onNext(null)
        source.onNext(null)
        source.onNext(4)

        observer.assertValues(null, 4)
    }
}
