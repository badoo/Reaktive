package com.badoo.reaktive.observable

import com.badoo.reaktive.base.SourceTests
import com.badoo.reaktive.base.SourceTestsImpl
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.single.test
import kotlin.test.Ignore
import kotlin.test.Test

interface ObservableToSingleTests : SourceTests {

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_completed()
}

@Ignore
class ObservableToSingleTestsImpl(
    transform: Observable<*>.() -> Single<*>
) : ObservableToSingleTests, SourceTests by SourceTestsImpl(TestObservable<Nothing>(), { transform().test() }) {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestObservable<Nothing>()
    private val observer = upstream.transform().test()

    override fun disposes_downstream_disposable_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertDisposed()
    }
}
