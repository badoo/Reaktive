package com.badoo.reaktive.observable

import com.badoo.reaktive.base.SourceTests
import com.badoo.reaktive.base.SourceTestsImpl
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.test.Ignore
import kotlin.test.Test

interface ObservableToCompletableTests : SourceTests {

    @Test
    fun completes_WHEN_upstream_is_completed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_completed()
}

@Ignore
class ObservableToCompletableTestsImpl(
    transform: Observable<*>.() -> Completable
) : ObservableToCompletableTests, SourceTests by SourceTestsImpl(TestObservable<Nothing>(), { transform().test() }) {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestObservable<Nothing>()
    private val observer = upstream.transform().test()

    override fun completes_WHEN_upstream_is_completed() {
        upstream.onComplete()

        observer.assertComplete()
    }

    override fun disposes_downstream_disposable_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertDisposed()
    }
}
