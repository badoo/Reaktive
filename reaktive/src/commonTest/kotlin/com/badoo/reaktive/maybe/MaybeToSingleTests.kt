package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.SourceTests
import com.badoo.reaktive.base.SourceTestsImpl
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.single.test
import kotlin.test.Ignore
import kotlin.test.Test

interface MaybeToSingleTests : SourceTests {

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_completed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_succeeded()
}

@Ignore
class MaybeToSingleTestsImpl(
    transform: Maybe<Unit>.() -> Single<*>
) : MaybeToSingleTests, SourceTests by SourceTestsImpl(TestMaybe<Nothing>(), { transform().test() }) {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestMaybe<Unit>()
    private val observer = upstream.transform().test()

    override fun disposes_downstream_disposable_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertDisposed()
    }

    override fun disposes_downstream_disposable_WHEN_upstream_succeeded() {
        upstream.onSuccess(Unit)

        observer.assertDisposed()
    }
}
