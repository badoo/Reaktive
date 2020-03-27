package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Ignore
import kotlin.test.Test

interface ObservableToObservableForwardTests {

    @Test
    fun forwards_values_from_upstream()
}

@Ignore
class ObservableToObservableForwardTestsImpl(
    transform: Observable<Int?>.() -> Observable<Int?>
) : ObservableToObservableForwardTests {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestObservable<Int?>()
    private val observer = upstream.transform().test()

    override fun forwards_values_from_upstream() {
        upstream.onNext(0, null, 1, null, 2)

        observer.assertValues(0, null, 1, null, 2)
    }
}
