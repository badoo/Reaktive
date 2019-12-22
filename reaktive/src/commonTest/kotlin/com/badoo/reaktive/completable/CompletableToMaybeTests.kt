package com.badoo.reaktive.completable

import com.badoo.reaktive.base.SourceTests
import com.badoo.reaktive.base.SourceTestsImpl
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Ignore
import kotlin.test.Test

interface CompletableToMaybeTests : SourceTests {

    @Test
    fun completes_WHEN_upstream_completed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_completed()
}

@Ignore
class CompletableToMaybeTestsImpl(
    transform: Completable.() -> Maybe<*>
) : CompletableToMaybeTests, SourceTests by SourceTestsImpl(TestCompletable(), { transform().test() }) {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestCompletable()
    private val observer = upstream.transform().test()

    override fun completes_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertComplete()
    }

    override fun produces_error_WHEN_upstream_produced_error() {
        val error = Throwable()

        upstream.onError(error)

        observer.assertError(error)
    }

    override fun disposes_downstream_disposable_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertDisposed()
    }
}
