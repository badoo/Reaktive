package com.badoo.reaktive.single

import com.badoo.reaktive.base.SourceTests
import com.badoo.reaktive.base.SourceTestsImpl
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Ignore
import kotlin.test.Test

interface SingleToMaybeTests : SourceTests {

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_succeeded()
}

@Ignore
class SingleToMaybeTestsImpl(
    transform: Single<Unit>.() -> Maybe<*>
) : SingleToMaybeTests, SourceTests by SourceTestsImpl(TestSingle<Nothing>(), { transform().test() }) {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestSingle<Unit>()
    private val observer = upstream.transform().test()

    override fun disposes_downstream_disposable_WHEN_upstream_succeeded() {
        upstream.onSuccess(Unit)

        observer.assertDisposed()
    }
}
