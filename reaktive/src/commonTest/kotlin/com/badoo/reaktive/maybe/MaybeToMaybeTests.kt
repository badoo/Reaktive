package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.SourceTests
import com.badoo.reaktive.base.SourceTestsImpl
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Ignore
import kotlin.test.Test

interface MaybeToMaybeTests : SourceTests {

    @Test
    fun completes_WHEN_upstream_is_completed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_completed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_succeeded()
}

@Ignore
class MaybeToMaybeTestsImpl(
    transform: Maybe<Unit>.() -> Maybe<*>
) : MaybeToMaybeTests, SourceTests by SourceTestsImpl(TestMaybe<Nothing>(), { transform().test() }) {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestMaybe<Unit>()
    private val observer = upstream.transform().test()

    override fun completes_WHEN_upstream_is_completed() {
        upstream.onComplete()

        observer.assertComplete()
    }

    override fun disposes_downstream_disposable_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertDisposed()
    }

    override fun disposes_downstream_disposable_WHEN_upstream_succeeded() {
        upstream.onSuccess(Unit)

        observer.assertDisposed()
    }
}
