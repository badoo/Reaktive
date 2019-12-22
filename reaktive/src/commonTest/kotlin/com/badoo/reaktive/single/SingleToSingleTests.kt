package com.badoo.reaktive.single

import com.badoo.reaktive.base.SourceTests
import com.badoo.reaktive.base.SourceTestsImpl
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import kotlin.test.Ignore
import kotlin.test.Test

interface SingleToSingleTests : SourceTests {

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_succeeded()
}

@Ignore
class SingleToSingleTestsImpl(
    transform: Single<Unit>.() -> Single<*>
) : SingleToSingleTests, SourceTests by SourceTestsImpl(TestSingle<Nothing>(), { transform().test() }) {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestSingle<Unit>()
    private val observer = upstream.transform().test()

    override fun disposes_downstream_disposable_WHEN_upstream_succeeded() {
        upstream.onSuccess(Unit)

        observer.assertDisposed()
    }
}
