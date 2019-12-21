package com.badoo.reaktive.completable

import com.badoo.reaktive.base.SourceTests
import com.badoo.reaktive.base.SourceTestsImpl
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.test
import kotlin.test.Ignore
import kotlin.test.Test

interface CompletableToObservableTests : SourceTests {

    @Test
    fun completes_WHEN_upstream_completed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_completed()
}

@Ignore
class CompletableToObservableTestsImpl(
    transform: Completable.() -> Observable<*>
) : CompletableToObservableTests, SourceTests by SourceTestsImpl(TestCompletable(), { transform().test() }) {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestCompletable()
    private val observer = upstream.transform().test()

    override fun completes_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertComplete()
    }

    override fun disposes_downstream_disposable_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertDisposed()
    }
}
